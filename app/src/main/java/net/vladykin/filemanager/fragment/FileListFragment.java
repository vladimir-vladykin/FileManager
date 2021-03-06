package net.vladykin.filemanager.fragment;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.pnikosis.materialishprogress.ProgressWheel;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.adapter.FileAdapter;
import net.vladykin.filemanager.adapter.FileHierarchyAdapter;
import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.presenter.FileListPresenter;
import net.vladykin.filemanager.util.FileActions;
import net.vladykin.filemanager.util.FileModule;
import net.vladykin.filemanager.util.FileUtils;
import net.vladykin.filemanager.util.OnFileItemClickListener;
import net.vladykin.filemanager.model.source.FilesSource;
import net.vladykin.filemanager.util.order.FileOrders;
import net.vladykin.filemanager.view.FileListView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static net.vladykin.filemanager.util.Permissions.READ_EXTERNAL_STORAGE_CODE;
import static net.vladykin.filemanager.util.Permissions.isReadExternalStorageAllowed;
import static net.vladykin.filemanager.util.Permissions.requestReadExternalStorage;
import static net.vladykin.filemanager.util.Permissions.shouldShowStorageRationale;

public final class FileListFragment extends BaseFragment
        implements OnFileItemClickListener.OnFileClickListener, FileListView {

    private static final String SOURCE = "source";

    @Bind(R.id.hierarchy_recycler_view) RecyclerView hierarchyRecyclerView;
    @Bind(R.id.file_list_view) RecyclerView recyclerView;
    @Bind(R.id.file_list_progress_bar) ProgressWheel progressBar;
    @Bind(R.id.file_list_empty_text) TextView emptyView;
    private SearchView searchView;

    @Inject FileListPresenter presenter;
    private FileAdapter adapter;
    private FileHierarchyAdapter hierarchyAdapter;

    public static FileListFragment newInstance(FilesSource source) {
        Bundle args = new Bundle();
        args.putSerializable(SOURCE, source);
        FileListFragment fragment = new FileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FilesSource source = (FilesSource) getArguments().getSerializable(SOURCE);
        appComponent()
                .addPresenterSubComponent(new FileModule(source))
                .inject(this);

        adapter = new FileAdapter(mActivity);
        hierarchyAdapter = new FileHierarchyAdapter(source, getString(R.string.root_directory_title));
        hierarchyAdapter.setHasStableIds(true);
        hierarchyAdapter.setClickListener(presenter);
        presenter.restoreState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setupRecyclerView();
        setupHierarchyRecyclerView();

        presenter.bindView(this);

        Activity activity = getActivity();
        if (isReadExternalStorageAllowed(activity)) {
            presenter.loadData();
        } else if (shouldShowStorageRationale(activity)) {
            showEmptyView();

            showMessageWithAction(
                    getString(R.string.permission_storage_rationale),
                    getString(R.string.permission_allow),
                    () -> requestReadExternalStorage(this)

            );
        } else {
            showEmptyView();
            requestReadExternalStorage(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarTitle(presenter.getSourceTitle());

        // we don't want to toolbar elevation overlap hierarchy list elevation
        // todo currently don't work
//        setToolbarElevation(0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // todo check is presenter bound
                presenter.loadData();
            }
        }
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);

        searchView = null;
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (searchMenuItem == null) {
            return;
        }

        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // todo check it, maybe we can loss observable after recreating view
                presenter.provideSearchObservable(RxSearchView.queryTextChanges(searchView));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_update:
                presenter.onRefreshClick();
                break;
            case R.id.action_new_file:
                presenter.onCreateFileClick();
                return true;
            case R.id.action_new_folder:
                presenter.onCreateDirectoryClick();
                return true;
            case R.id.action_sort:
                // fixme why not presenter decide should we show order ui?
                showOrderUi();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOrderUi() {
        FileOrders.prepareDialog(getActivity(), presenter)
                .build()
                .show();
    }

    @Override
    public void showLoading() {
        establishViewsVisibility(true, false, false);
    }

    @Override
    public void showFileList(List<FileItem> files) {
        establishViewsVisibility(false, true, false);
        adapter.setFilesInfo(files);

        // fixme presenter should decide, when to update hierarchy list
        updateHierarchyUi();

        // todo maybe not the best decision
        recyclerView.post(() -> recyclerView.scrollToPosition(0));
    }

    @Override
    public void showEmptyView() {
        // fixme should not be here, but in presenter
        updateHierarchyUi();

        establishViewsVisibility(false, false, true);
    }

    public void updateHierarchyUi() {
        hierarchyAdapter.notifyHierarchyUpdated();
        hierarchyRecyclerView.post(() ->
                hierarchyRecyclerView.scrollToPosition(hierarchyAdapter.getItemCount()));
    }

    @Override
    public void insertItem(int position) {
        adapter.notifyItemInserted(position);
    }

    @Override
    public void updateItem(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void removeItem(int position) {
        // todo we have to actually check should we show empty view or not (but presenter should check this rather then fragment)
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void moveItem(int from, int to) {
        adapter.notifyItemMoved(from, to);
    }

    @Override
    public void openFile(File file) {
        boolean isOpened = FileUtils.openFile(mActivity, file);

        // if we can't open file
        if(!isOpened) {
            Snackbar.make(recyclerView, R.string.no_app, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void setBackButtonVisible(boolean visible) {
        if (visible) {
            showBackButton();
        } else {
            hideBackButton();
        }
    }

    @Override
    public void showError(String message, Throwable error) {
        error.printStackTrace();
        // todo message from resources
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showError(@ErrorCode int errorCode, @Nullable Throwable cause) {
        if (cause != null) {
            cause.printStackTrace();
        }

        switch (errorCode) {
            case FILE_OPERATIONS_NOT_SUPPORTED:
                showSnackbarMessage(R.string.file_action_not_allowed);
                break;
        }
    }

    @Override
    public void showFileActionsUi(FileItem fileItem) {
        FileActions.prepareDialog(getActivity(), fileItem, presenter)
                .build()
                .show();
    }

    @Override
    public void showRenameUi(final FileItem fileItem) {
        final File file = fileItem.getFile();
        final String oldFileName = fileItem.getName();

        MaterialDialog.InputCallback callback = (dialog, input) -> {
            String newFileName = input.toString();

            if (!newFileName.isEmpty() && !newFileName.equals(oldFileName)) {
                presenter.renameFile(fileItem, newFileName);
            }
        };

        showInputDialog(
                R.string.enter_new_name, oldFileName,
                file.isDirectory() ? R.string.directory_name : R.string.file_name,
                callback);
    }

    @Override
    public void showCreateFileUi(boolean forDirectory) {
        showInputDialog(
                R.string.enter_file_name, "",
                forDirectory ? R.string.directory_name : R.string.file_name,
                (dialog, input) -> {
                    presenter.createFile(input.toString(), forDirectory);
                }
        );
    }

    @Override
    public void setInsertFileUiActive(boolean active) {
        if (!active) {
            getFloatingButtonController().hideActionButton();
            return;
        }

        getFloatingButtonController().showActionButton(button -> {
            presenter.onInsertFileButtonClick();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addOnItemTouchListener(
                new OnFileItemClickListener(mActivity, recyclerView, this));
        recyclerView.setAdapter(adapter);
    }

    private void setupHierarchyRecyclerView() {
        hierarchyRecyclerView.setLayoutManager(new LinearLayoutManager(
                mActivity, LinearLayoutManager.HORIZONTAL, false
        ));
        hierarchyRecyclerView.setAdapter(hierarchyAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hierarchyRecyclerView.setElevation(getResources()
                    .getDimensionPixelSize(R.dimen.toolbar_elevation));
        }
    }

    private void establishViewsVisibility(boolean progressVisible,
                                          boolean recyclerViewVisible,
                                          boolean emptyViewVisible) {
        progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(recyclerViewVisible ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        presenter.onFileClick(position);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        presenter.onFileLongClick(position);
    }

    @Override
    public boolean onBackPressed() {
        return presenter.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}

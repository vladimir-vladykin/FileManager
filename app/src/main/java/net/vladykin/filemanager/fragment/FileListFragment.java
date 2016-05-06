package net.vladykin.filemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pnikosis.materialishprogress.ProgressWheel;

import net.vladykin.filemanager.FileManagerApp;
import net.vladykin.filemanager.R;
import net.vladykin.filemanager.adapter.FileAdapter;
import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.presenter.FileListPresenter;
import net.vladykin.filemanager.util.FileActions;
import net.vladykin.filemanager.util.FileUtils;
import net.vladykin.filemanager.util.OnFileItemClickListener;
import net.vladykin.filemanager.view.FileListView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class FileListFragment extends BaseFragment
        implements OnFileItemClickListener.OnFileClickListener, FileListView {

    private static final String FILES_KEY = "files_key";
    private static final String DIRECTORY_KEY = "directory_key";

    @Bind(R.id.file_list_view) RecyclerView recyclerView;
    @Bind(R.id.file_list_progress_bar) ProgressWheel progressBar;
    @Bind(R.id.file_list_empty_text) TextView emptyView;

    @Inject FileListPresenter presenter;
    private FileAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileManagerApp.component().inject(this);

        adapter = new FileAdapter(mActivity);
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

        presenter.bindView(this);
        presenter.loadData();
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    @Override
    public void showLoading() {
        establishViewsVisibility(true, false, false);
    }

    @Override
    public void showFileList(List<FileItem> files) {
        establishViewsVisibility(false, true, false);
        adapter.setFilesInfo(files);

        // todo maybe not the best decision
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void showEmptyView() {
        establishViewsVisibility(false, false, true);
    }

    @Override
    public void updateItem(int position) {
        adapter.notifyItemChanged(position);
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
        // todo message from resources
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showFileActionsUi(FileItem fileItem) {
        FileActions.prepareDialog(getContext(), fileItem, presenter)
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

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addOnItemTouchListener(
                new OnFileItemClickListener(mActivity, recyclerView, this));
        recyclerView.setAdapter(adapter);
    }

    private void establishViewsVisibility(boolean progressVisible,
                                          boolean recyclerViewVisible,
                                          boolean emptyViewVisible) {
        progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(recyclerViewVisible ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
    }

//    private void removeFile(final int position) {
//        File file = mFilesInfo.get(position).getFile();
//
//        FileUtils.deleteFile(file, new FileUtils.CompleteListener() {
//            @Override
//            public void onComplete() {
//                // update list
//                mFilesInfo.onRemove(position);
//                adapter.notifyRemoved(position);
//            }
//
//            @Override
//            public void onFailure() {
//                Toast.makeText(
//                        mActivity, R.string.file_cannot_delete, Toast.LENGTH_LONG
//                ).show();
//            }
//        });
//    }
//
//    private void renameFile(int position, String newName) {
//        File file = mFilesInfo.get(position).getFile();
//
//        if (FileUtils.renameFile(file, newName)) {
//            // update info about file
////            FileItem newFileInfo = new FileItem(new File(newName));
//            mFilesInfo.get(position).setName(newName);
//            adapter.notifyItemChanged(position);
//        } else {
//            showMessage(R.string.error, R.string.file_cannot_rename);
//        }
//    }

    @Override
    public void onItemClick(View view, int position) {
        presenter.onFileClick(position);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        presenter.onFileLongClick(position);
//        mContextDialog.bindToItem(position);
//        mContextDialog.setContextItemClickListener(new FileContextDialogView.ContextItemClickListener() {
//            @Override
//            public void onOpenClick(int position) {
//                mContextDialog.dismiss();
//                File file = adapter.getFileInfo(position).getFile();
//                openFile(file);
//            }
//
//            @Override
//            public void onCopyClick(int position) {
//                mContextDialog.dismiss();
//            }
//
//            @Override
//            public void onMoveClick(int position) {
//                mConte
//
//            @OverridextDialog.dismiss();
//            }
//            public void onRenameClick(final int position) {
//                mContextDialog.dismiss();
//                final File file = adapter.getFileInfo(position).getFile();
//                final String oldFileName = adapter.getFileInfo(position).getName();
//
//                showInputDialog(
//                        R.string.enter_new_name,
//                        oldFileName,
//                        file.isDirectory() ? R.string.directory_name : R.string.file_name,
//                        new MaterialDialog.InputCallback() {
//                            @Override
//                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
//                                String fileName = charSequence.toString();
//
//                                // if it is new file name
//                                if (!fileName.equals(oldFileName)) {
//                                    renameFile(position, fileName);
//                                }
//                            }
//                });
//            }
//
//            @Override
//            public void onRemoveClick(int position) {
//                mContextDialog.dismiss();
//                removeFile(position);
//
//            }
//        });
//        mContextDialog.show();
    }

    @Override
    public boolean onBackPressed() {
        return presenter.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // todo save presenter state
        super.onSaveInstanceState(outState);
        //TODO mFileInfo can be empty while loading
//        outState.putSerializable(FILES_KEY, mFilesInfo);
//        outState.putSerializable(DIRECTORY_KEY, mCurrentDirectory);
    }
}

package net.vladykin.filemanager.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rey.material.widget.ProgressView;

import net.vladykin.filemanager.adapter.FileAdapter;
import net.vladykin.filemanager.util.OnFileItemClickListener;
import net.vladykin.filemanager.R;
import net.vladykin.filemanager.model.FileInfo;
import net.vladykin.filemanager.util.FileContextDialog;
import net.vladykin.filemanager.util.FileInfoCreatorTask;
import net.vladykin.filemanager.util.FileUtils;
import net.vladykin.filemanager.view.FileContextDialogView;

import java.io.File;
import java.util.ArrayList;

public class FileListFragment extends BaseFragment
implements OnFileItemClickListener.OnFileClickListener,
        FileInfoCreatorTask.OnFileInfoCreationListener, View.OnClickListener {

    private static final String FILES_KEY = "files_key";
    private static final String DIRECTORY_KEY = "directory_key";

    private FloatingActionButton mUpdateButton;
    private RecyclerView mListView;
    private ProgressWheel mProgressBar;
    private TextView mEmptyTextView;
    private FileAdapter mAdapter;
    private ArrayList<FileInfo> mFilesInfo;
    private File mCurrentDirectory;
    private FileInfoCreatorTask mCreatorTask;

    private FileContextDialog mContextDialog;

    /**
     * For show enter animation once after user have opened app.
     */
    private boolean isPendingIntroAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            isPendingIntroAnimation = true;
        }
        mAdapter = new FileAdapter(mActivity, isPendingIntroAnimation);
        mContextDialog = new FileContextDialog(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        mProgressBar = (ProgressWheel) view.findViewById(R.id.file_list_progress_bar);
        mEmptyTextView = (TextView) view.findViewById(R.id.file_list_empty_text);

        setupRecyclerView(view);

        mUpdateButton = (FloatingActionButton) view.findViewById(R.id.file_list_update_button);
        mUpdateButton.setOnClickListener(this);

        if(isPendingIntroAnimation) {
            mUpdateButton.hide(false);
        } else {
            mUpdateButton.show(false);
            mUpdateButton.attachToRecyclerView(mListView);
        }

        //if we saved files
        if(savedInstanceState != null) {
            mFilesInfo = (ArrayList<FileInfo>) savedInstanceState.getSerializable(FILES_KEY);
            mCurrentDirectory = (File) savedInstanceState.getSerializable(DIRECTORY_KEY);
        } else {
            mFilesInfo = new ArrayList<>();
            //fill mFilesInfo with files from root directory
            initFileArray();
        }
        mAdapter.setFilesInfo(mFilesInfo);

        initToolbar();
//        startIntoIfNecessary(view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mCreatorTask != null) {
            mCreatorTask.cancel(true);
        }

        if(mContextDialog != null) {
            mContextDialog.dismiss();
        }
    }

    private void setupRecyclerView(View view) {
        mListView = (RecyclerView) view.findViewById(R.id.file_list_view);
        mListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        mListView.addOnItemTouchListener(
                new OnFileItemClickListener(mActivity, mListView, this));
        mListView.setAdapter(mAdapter);
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // we need this code only once
                    mListView.removeOnScrollListener(this);

                    Log.d(getClass().getSimpleName(), "disable enter animation");
                    //disable animation after scrolling
                    mAdapter.setWillEnterAnimation(false);
                    mUpdateButton.attachToRecyclerView(mListView);
                }
            }
        });
    }

    private void initToolbar() {
        //set listener to back button for detect click
        Toolbar toolbar = mActivity.getToolbar();
        toolbar.setNavigationOnClickListener(new BackButtonListener());

        if(isRootDirectory(mCurrentDirectory)) {
            //on root directory we don't need back button
            hideBackButton();
        } else {
            showBackButton();
        }
    }

    private void initFileArray() {
        //get root directory
        mCurrentDirectory = FileUtils.getRootDirectory();

        //create list of files in root directory
        updateInfoArray(mCurrentDirectory);
    }

    private void updateList(File directory) {
        mCurrentDirectory = directory;
        updateInfoArray(mCurrentDirectory);
    }

    /**
     * Start task for create array of information about files.
     *
     * @param parentFile Directory, which we will use for get list of files.
     */
    private void updateInfoArray(File parentFile) {
        //if task work now
        if(mCreatorTask != null) {
            mCreatorTask.cancel(true);
        }

        //create task and set listener
        mCreatorTask = new FileInfoCreatorTask(parentFile, this);
        mCreatorTask.execute();
    }

    @Override
    public void onStartCreation(boolean isLongOperation) {
        //hide empty text
        mEmptyTextView.setVisibility(View.GONE);

        //if we will load new file long time
        if(isLongOperation) {
            //hide list view and show loader
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreated(ArrayList<FileInfo> filesInfo) {
        mProgressBar.setVisibility(View.GONE);

        mFilesInfo = filesInfo;
        mAdapter.setFilesInfo(mFilesInfo);

        if(mFilesInfo.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_list_update_button:
                updateInfoArray(mCurrentDirectory);
                break;
        }
    }

    private void openFile(File file) {
        //if it is folder, go inside
        if(file.isDirectory()) {
//            mListView.scrollToPosition(0);
            updateList(file);
            showBackButton();
        } else {
            boolean isOpened = FileUtils.openFile(mActivity, file);

            //if we can't open file
            if(!isOpened) {
                Toast.makeText(mActivity, R.string.no_app, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeFile(final int position) {
        File file = mFilesInfo.get(position).getFile();

        FileUtils.deleteFile(file, new FileUtils.CompleteListener() {
            @Override
            public void onComplete() {
                // update list
                mFilesInfo.remove(position);
                mAdapter.notifyRemoved(position);
            }

            @Override
            public void onFailure() {
                Toast.makeText(
                        mActivity, R.string.file_cannot_delete, Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void renameFile(int position, String newName) {
        File file = mFilesInfo.get(position).getFile();

        if (FileUtils.renameFile(file, newName)) {
            // update info about file
//            FileInfo newFileInfo = new FileInfo(new File(newName));
            mFilesInfo.get(position).setName(newName);
            mAdapter.notifyItemChanged(position);
        } else {
            showMessage(R.string.error, R.string.file_cannot_rename);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        File file = mAdapter.getFileInfo(position).getFile();
        openFile(file);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        mContextDialog.bindToItem(position);
        mContextDialog.setContextItemClickListener(new FileContextDialogView.ContextItemClickListener() {
            @Override
            public void onOpenClick(int position) {
                mContextDialog.dismiss();
                File file = mAdapter.getFileInfo(position).getFile();
                openFile(file);
            }

            @Override
            public void onCopyClick(int position) {
                mContextDialog.dismiss();
            }

            @Override
            public void onMoveClick(int position) {
                mContextDialog.dismiss();
            }

            @Override
            public void onRenameClick(final int position) {
                mContextDialog.dismiss();
                final File file = mAdapter.getFileInfo(position).getFile();
                final String oldFileName = mAdapter.getFileInfo(position).getName();

                showInputDialog(
                        R.string.enter_new_name,
                        oldFileName,
                        file.isDirectory() ? R.string.directory_name : R.string.file_name,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                String fileName = charSequence.toString();

                                // if it is new file name
                                if (!fileName.equals(oldFileName)) {
                                    renameFile(position, fileName);
                                }
                            }
                });
            }

            @Override
            public void onRemoveClick(int position) {
                mContextDialog.dismiss();
                removeFile(position);

            }
        });
        mContextDialog.show();
    }



    private class BackButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            processOnBack();
        }
    }

    @Override
    public boolean onBackPressed() {
        //if it is current directory
        if(isRootDirectory(mCurrentDirectory)) {
            //go out of this fragment
            return false;
        }

        processOnBack();
        return true;
    }

    private void processOnBack() {
        File parentDirectory = mCurrentDirectory.getParentFile();
        updateList(parentDirectory);

        //if it is root directory
        if(isRootDirectory(parentDirectory)) {
            hideBackButton();
        }
    }

    private boolean isRootDirectory(File directory) {
        return FileUtils.isRootDirectory(directory);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO mFileInfo can be empty while loading
        outState.putSerializable(FILES_KEY, mFilesInfo);
        outState.putSerializable(DIRECTORY_KEY, mCurrentDirectory);
    }

    @Override
    public void onAnimationEnd() {
        if(isPendingIntroAnimation) {
            isPendingIntroAnimation = false;
            mUpdateButton.hide(false);

            mAdapter.showItems();

            mUpdateButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                  mUpdateButton.show(true);
                }
            }, 500);
        }
    }
}

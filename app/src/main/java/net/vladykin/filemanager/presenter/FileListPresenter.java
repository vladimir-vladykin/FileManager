package net.vladykin.filemanager.presenter;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.model.FileModel;
import net.vladykin.filemanager.util.FileActionsCallbacks;
import net.vladykin.filemanager.util.FileManager;
import net.vladykin.filemanager.view.FileListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Single;
import rx.Subscription;

/**
 * Presenter for view with file list.
 *
 * @author Vladimir Vladykin.
 */
public final class FileListPresenter extends Presenter<FileListView>
        implements FileActionsCallbacks {

    @NonNull private final FileModel model;
    @NonNull private final FileManager fileManager;
    @NonNull private final File rootDirectory;

    private List<FileItem> items;
    private File currentDirectory;

    @Inject
    public FileListPresenter(@NonNull FileModel model,
                             @NonNull FileManager fileManager,
                             @NonNull File rootDirectory) {
        this.model = model;
        this.fileManager = fileManager;
        this.rootDirectory = rootDirectory;

        currentDirectory = rootDirectory;
        items = new ArrayList<>();
    }

    @Override
    public void bindView(@NonNull FileListView view) {
        super.bindView(view);

        // initially we don't need back button
        setViewBackButtonVisible(false);
    }

    public void loadData() {
        showViewLoading();

        Subscription subscription = model
                .getFiles(currentDirectory)
                .subscribe(
                        this::saveFilesAndSetToView,
                        throwable ->
                                showErrorAndEmptyView("Cannot load files list", throwable)
                );

        unsubcribeAfterUnbind(subscription);
    }

    public void onFileClick(int position) {
        File file = items.get(position).getFile();
        dispatchFileOpening(file);
    }

    public void onFileLongClick(int position) {
        view().showFileActionsUi(items.get(position));
    }

    /**
     * Returns true, if event was handled, false otherwise.
     */
    public boolean onBackPressed() {
        if (isRootDirectory(currentDirectory)) {
            // presenter doesn't know, how to deal with
            // back click in such situation
            return false;
        }

        dispatchOnBack();
        return true;
    }

    public void renameFile(final FileItem oldFileItem, String newFileName) {
        // todo probably blocking progress here
        Subscription subscription = fileManager.rename(oldFileItem.getFile(), newFileName)
                .subscribe(
                        newFile -> {
                            // todo maybe move to another thread via flatMap and obtain here prepared FileItem
                            FileItem newFileItem = new FileItem(newFile);
                            int indexOfOldFile = items.indexOf(newFileItem);
                            items.set(indexOfOldFile, newFileItem);
                            view().updateItem(indexOfOldFile);
                        },
                        throwable ->
                                showErrorAndEmptyView("Cannot rename file", throwable)
                );

        unsubcribeAfterUnbind(subscription);
    }

    public void createFile(String fileName, boolean shouldCreateDirectory) {
        Single<File> creatingObservable = shouldCreateDirectory
                ? fileManager.createDirectory(currentDirectory, fileName)
                : fileManager.createFile(currentDirectory, fileName);

        Subscription subscription = creatingObservable
                .subscribe(
                        createdFile -> {
//                            items.add(new FileItem(createdFile));
                            // we just reload data here
                            loadData();
                        },
                        throwable ->
                                view().showError("Cannot create file", throwable)
                );

        unsubcribeAfterUnbind(subscription);
    }

    public void onCreateFileClick() {
        view().showCreateFileUi(false);
    }

    public void onCreateDirectoryClick() {
        view().showCreateFileUi(true);
    }

    @Override /** @hide */
    public void onOpen(FileItem fileItem) {
        dispatchFileOpening(fileItem.getFile());
    }

    @Override /** @hide */
    public void onCopy(FileItem fileItem) {
//        fileManager.copy(file);
    }

    @Override /** @hide */
    public void onMove(FileItem fileItem) {
//        fileManager.move(file);
    }

    @Override /** @hide */
    public void onRename(FileItem fileItem) {
        view().showRenameUi(fileItem);
    }

    @Override /** @hide */
    public void onRemove(FileItem fileItem) {
//        fileManager.remove(file);
    }

    private boolean isRootDirectory(File directory) {
        return rootDirectory.equals(directory);
    }

    private void openDirectory(File directory) {
        currentDirectory = directory;

        boolean shouldShowBackButton = !isRootDirectory(currentDirectory);
        setViewBackButtonVisible(shouldShowBackButton);

        loadData();
    }

    private void showViewLoading() {
        FileListView view = view();
        if (view != null) {
            view.showLoading();
        }
    }

    private void saveFilesAndSetToView(List<FileItem> files) {
        items = files;

        FileListView view = view();
        if (view == null) {
            return;
        }

        if (files.size() > 0) {
            view.showFileList(files);
        } else {
            view.showEmptyView();
        }
    }

    private void showErrorAndEmptyView(String message, Throwable throwable) {
        FileListView view = view();
        if (view != null) {
            view.showError(message, throwable);
            view.showEmptyView();
        }
    }

    private void setViewBackButtonVisible(boolean visible) {
        FileListView view = view();
        if (view != null) {
            view.setBackButtonVisible(visible);
        }
    }

    private void dispatchFileOpening(File file) {
        if (file.isDirectory()) {
            openDirectory(file);
        } else {
            FileListView view = view();
            if (view == null) {
                return;
            }

            view.openFile(file);
        }
    }

    private void dispatchOnBack() {
        File parentDirectory = currentDirectory.getParentFile();
        openDirectory(parentDirectory);
    }
}

package net.vladykin.filemanager.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.entity.Node;
import net.vladykin.filemanager.model.FileModel;
import net.vladykin.filemanager.util.FileActionsCallbacks;
import net.vladykin.filemanager.util.FileManager;
import net.vladykin.filemanager.model.source.FilesSource;
import net.vladykin.filemanager.util.callback.HierarchyNodeClickListener;
import net.vladykin.filemanager.util.order.FileOrdersCallback;
import net.vladykin.filemanager.view.FileListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static net.vladykin.filemanager.util.order.FileItemComparators.alphabet;

/**
 * Presenter for view with file list.
 * FIXME don't check is currentDirectory is null, but display only allowed actions in FileListView!!
 * @author Vladimir Vladykin.
 */
public final class FileListPresenter extends Presenter<FileListView>
        implements FileActionsCallbacks, FileOrdersCallback, HierarchyNodeClickListener {

    private static final int SEARCH_DELAY = 50;
    private static final String CURRENT_DIRECTORY_KEY = "file_key";

    @NonNull private final FileModel model;
    @NonNull private final FileManager fileManager;
    @NonNull private final FilesSource root;

    private final List<FileItem> originalItems;
    private final List<FileItem> filteredItems;
    @Nullable private File currentDirectory;

    private Comparator<FileItem> comparator;
    private CharSequence searchKey;

    private boolean wasFirstSearchKeyPassed;
    @Nullable private FileItem itemToCopy;
    @Nullable private FileItem itemToMove;

    @Inject
    public FileListPresenter(@NonNull FileModel model,
                             @NonNull FileManager fileManager,
                             @NonNull FilesSource root) {
        this.model = model;
        this.fileManager = fileManager;
        this.root = root;

        currentDirectory = root.getRootDirectory();
        originalItems = new ArrayList<>();
        filteredItems = new ArrayList<>();
        comparator = alphabet();
    }

    @Override
    public void bindView(@NonNull FileListView view) {
        super.bindView(view);

        setViewBackButtonVisible(true/*!root.isRootDirectory(currentDirectory)*/);
    }

    public void loadData() {
        showViewLoading();

        Subscription subscription = model
                .getFiles()
                .subscribe(
                        this::saveFilesAndSetToView,
                        throwable ->
                                showErrorAndEmptyView("Cannot load files list", throwable)
                );

        unsubcribeAfterUnbind(subscription);
    }

    public void saveState(Bundle outState) {
        if (currentDirectory == null) {
            return;
        }
        // todo current search key and comparator could be saved too
        outState.putSerializable(CURRENT_DIRECTORY_KEY, currentDirectory);
    }

    public void restoreState(@Nullable Bundle savedState) {
        if (savedState != null && savedState.containsKey(CURRENT_DIRECTORY_KEY)) {
            currentDirectory = (File) savedState.getSerializable(CURRENT_DIRECTORY_KEY);
            root.setCurrentDirectory(currentDirectory);
        }
    }

    public void onFileClick(int position) {
        File file = filteredItems.get(position).getFile();
        dispatchFileOpening(file);
    }

    public void onFileLongClick(int position) {
        view().showFileActionsUi(filteredItems.get(position));
    }

    public CharSequence getSourceTitle() {
        return root.title();
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
        Subscription subscription = fileManager.rename(oldFileItem.getFile(), newFileName)
                .subscribe(
                        newFile -> {
                            FileItem newFileItem = new FileItem(newFile);

                            // change item in original list
                            int indexOfOldFile = originalItems.indexOf(oldFileItem);
                            originalItems.set(indexOfOldFile, newFileItem);

                            int indexInFilteredListBeforeFiltering = filteredItems.indexOf(oldFileItem);

                            filterItems();
                            reorderItems();

                            // ofter filtering filteredItems contains newFileItem instead of oldFileItem
                            int indexInFilteredListAfterFiltering = filteredItems.indexOf(newFileItem);

                            if (indexInFilteredListAfterFiltering != indexInFilteredListBeforeFiltering) {
                                view().updateItem(indexInFilteredListBeforeFiltering);
                                view().updateItem(indexInFilteredListAfterFiltering);
                                view().moveItem(
                                        indexInFilteredListBeforeFiltering,
                                        indexInFilteredListAfterFiltering);
                            } else {
                                // position the same, so just update
                                view().updateItem(indexInFilteredListAfterFiltering);
                            }
                        },
                        throwable ->
                                // fixme really??? why we need empty view in that case???
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
//                            filteredItems.add(new FileItem(createdFile));
                            // todo we have to insert item here
                            // we just reload data here
                            loadData();
                        },
                        throwable ->
                                view().showError("Cannot create file", throwable)
                );

        unsubcribeAfterUnbind(subscription);
    }

    public void onRefreshClick() {
        loadData();
    }

    public void onCreateFileClick() {
        if (!canDoFileOperations()) {
            displayFileOperationsNotSupported();
            return;
        }
        view().showCreateFileUi(false);
    }

    public void onCreateDirectoryClick() {
        if (!canDoFileOperations()) {
            displayFileOperationsNotSupported();
            return;
        }
        view().showCreateFileUi(true);
    }

    public void provideSearchObservable(@NonNull Observable<CharSequence> searchObservable) {
        Subscription subscription = searchObservable
                .debounce(SEARCH_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchKey -> {
                            this.searchKey = searchKey;
                            if (!wasFirstSearchKeyPassed) {
                                wasFirstSearchKeyPassed = true;
                                return;
                            }

                            filterItems();
                            reorderItems();
                            if (filteredItems.size() > 0) {
                                view().showFileList(filteredItems);
                            } else {
                                view().showEmptyView();
                            }
                        }, // todo don't stop observable after some error occurs
                        throwable -> view().showError("Cannot perform search", throwable)
                );
        unsubcribeAfterUnbind(subscription);
    }

    @Override
    public void onOrderChosen(Comparator<FileItem> newComparator) {
        if (comparator != newComparator) {
            comparator = newComparator;

            filterItems(); // todo needed?
            // we don't need to filter items again here, we only chane the order
            reorderItems();
            view().showFileList(filteredItems);
        }
    }

    @Override
    public void onHierarchyNodeClick(Node node) {
        File directory = node.getDirectory();
        if (directory == null || currentDirectory == null) {
            // nothing to do here, it is probably
            // click on root folder in images list, for instance
            return;
        }

        if (currentDirectory.equals(directory)) {
            // already opened, nothing to do
            return;
        }

        if (!directory.isDirectory() || !directory.exists()) {
            throw new IllegalStateException("Node should be a directory and this directory should be created before");
        }

        openDirectory(directory);
    }

    public void onInsertFileButtonClick() {
        if (itemToMove == null && itemToCopy == null) {
            Log.e("FileListPresenter", "We have to have itemToMove or itemToCopy for perform instert." +
                    " How you actually called this method???");
            return;
        }

        boolean shouldMoveItem = itemToMove != null;
        File from = shouldMoveItem ? itemToMove.getFile() : itemToCopy.getFile();
        File to = new File(currentDirectory, from.getName());

        if (to.equals(from)) {
            view().showError("Cannot insert file here", new UnsupportedOperationException());
            return;
        }

        Single<File> single = shouldMoveItem ?
                fileManager.move(from, to) : fileManager.copy(from, to);
        Subscription subscription = single.subscribe(
                insertedItem -> {
                    FileItem newFileItem = new FileItem(insertedItem);
                    originalItems.add(newFileItem);

                    filterItems();
                    reorderItems();

                    int newItemIndex = filteredItems.indexOf(newFileItem);
                    view().setInsertFileUiActive(false);

                    // FIXME we should only call for insert item, but currently we have the problem with inserting in invisible empty RecyclerView
                    if (filteredItems.size() == 1) {
                        // first inserting
                        view().showFileList(filteredItems);
                    } else {
                        view().insertItem(newItemIndex);
                    }
                },
                throwable ->
                        view().showError(
                                "Cannot insert file:\n" + throwable.getMessage(),
                                throwable)
        );

        unsubcribeAfterUnbind(subscription);
    }

    @Override /** @hide */
    public void onOpen(FileItem fileItem) {
        dispatchFileOpening(fileItem.getFile());
    }

    @Override /** @hide */
    public void onCopy(FileItem fileItem) {
        if (!canDoFileOperations()) {
            displayFileOperationsNotSupported();
            return;
        }

        itemToCopy = fileItem;
        view().setInsertFileUiActive(true);
    }

    @Override /** @hide */
    public void onMove(FileItem fileItem) {
        if (!canDoFileOperations()) {
            displayFileOperationsNotSupported();
            return;
        }

        itemToMove = fileItem;
        view().setInsertFileUiActive(true);
    }

    @Override /** @hide */
    public void onRename(FileItem fileItem) {
        view().showRenameUi(fileItem);
    }

    @Override /** @hide */
    public void onRemove(FileItem removingFileItem) {
        unsubcribeAfterUnbind(fileManager.remove(removingFileItem.getFile())
                .subscribe(
                        aVoid -> {
                            final int previousItemPosition = originalItems.indexOf(removingFileItem);
                            if (previousItemPosition < 0) {
                                return;
                            }

                            originalItems.remove(previousItemPosition);

                            final int oldItemPositionInFilteredList = filteredItems.indexOf(removingFileItem);
                            if (oldItemPositionInFilteredList < 0) {
                                Log.wtf("FileListPresenter", "Something wrong with removing from filtered items");
                            }

                            // we don't do any filtering or reordering, just manually remove
                            // item from filtered list
                            filteredItems.remove(oldItemPositionInFilteredList);
                            view().removeItem(oldItemPositionInFilteredList);
                        },
                        throwable ->
                                view().showError("Cannot delete file", throwable)
                )
        );
    }

    private boolean isRootDirectory(File directory) {
        return root.isRootDirectory(directory) /*rootDirectory.equals(directory)*/;
    }

    private void openDirectory(File directory) {
        if (!canDoFileOperations()) {
            displayFileOperationsNotSupported();
            return;
        }

        currentDirectory = directory;
        root.setCurrentDirectory(currentDirectory);

        boolean shouldShowBackButton = !isRootDirectory(currentDirectory);

        // currently we always display back button, because
        // we have to keep in mind FilesSourceFragment.
        // todo dispatch nicely in future, or not dispatch at all
        setViewBackButtonVisible(true/*shouldShowBackButton*/);

        loadData();
    }

    private void reorderItems() {
        Collections.sort(filteredItems, comparator);
    }

    private void filterItems() {
        filteredItems.clear();
        if (searchKey == null || searchKey.length() == 0) {
            filteredItems.addAll(originalItems);
            return;
        }

        String upperCaseConstraint = searchKey.toString().toUpperCase(Locale.getDefault());
        for (int i = 0, count = originalItems.size(); i < count; i++) {
            FileItem fileItem = originalItems.get(i);
            if (itemSuitable(upperCaseConstraint, fileItem.getName())) {
                filteredItems.add(fileItem);
            }
        }
    }

    private boolean itemSuitable(String constraint, String itemName) {
        return itemName.toUpperCase(Locale.getDefault()).startsWith(constraint);
    }

    private void showViewLoading() {
        view().showLoading();
    }

    private void saveFilesAndSetToView(List<FileItem> files) {
        originalItems.clear();
        originalItems.addAll(files);

        if (originalItems.size() > 0) {
            filterItems();
            reorderItems();
            view().showFileList(filteredItems);
        } else {
            view().showEmptyView();
        }
    }

    private void showErrorAndEmptyView(String message, Throwable throwable) {
        view().showError(message, throwable);
        view().showEmptyView();
    }

    private void setViewBackButtonVisible(boolean visible) {
        view().setBackButtonVisible(visible);
    }

    private void dispatchFileOpening(File file) {
        if (file.isDirectory()) {
            openDirectory(file);
        } else {
            view().openFile(file);
        }
    }

    private void dispatchOnBack() {
        if (currentDirectory == null) {
            return;
        }
        File parentDirectory = currentDirectory.getParentFile();
        openDirectory(parentDirectory);
    }

    // TODO this operator is actually hack, we should not display actions which are not supported for current mode
    private boolean canDoFileOperations() {
        return currentDirectory != null;
    }

    private void displayFileOperationsNotSupported() {
        view().showError(FileListView.FILE_OPERATIONS_NOT_SUPPORTED, null);
    }
}

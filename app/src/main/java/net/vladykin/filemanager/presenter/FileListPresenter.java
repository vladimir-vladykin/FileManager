package net.vladykin.filemanager.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.model.FileModel;
import net.vladykin.filemanager.util.FileActionsCallbacks;
import net.vladykin.filemanager.util.FileManager;
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
 *
 * @author Vladimir Vladykin.
 */
public final class FileListPresenter extends Presenter<FileListView>
        implements FileActionsCallbacks, FileOrdersCallback {

    private static final int SEARCH_DELAY = 50;

    @NonNull private final FileModel model;
    @NonNull private final FileManager fileManager;
    @NonNull private final File rootDirectory;

    private List<FileItem> originalItems;
    private List<FileItem> filteredItems;
    private File currentDirectory;

    private Comparator<FileItem> comparator;
    private CharSequence searchKey;

    private boolean wasFirstSearchKeyPassed;

    @Inject
    public FileListPresenter(@NonNull FileModel model,
                             @NonNull FileManager fileManager,
                             @NonNull File rootDirectory) {
        this.model = model;
        this.fileManager = fileManager;
        this.rootDirectory = rootDirectory;

        currentDirectory = rootDirectory;
        originalItems = new ArrayList<>();
        filteredItems = new ArrayList<>();
        comparator = alphabet();
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
        File file = filteredItems.get(position).getFile();
        dispatchFileOpening(file);
    }

    public void onFileLongClick(int position) {
        view().showFileActionsUi(filteredItems.get(position));
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

    public void onCreateFileClick() {
        view().showCreateFileUi(false);
    }

    public void onCreateDirectoryClick() {
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
        return rootDirectory.equals(directory);
    }

    private void openDirectory(File directory) {
        currentDirectory = directory;

        boolean shouldShowBackButton = !isRootDirectory(currentDirectory);
        setViewBackButtonVisible(shouldShowBackButton);

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
        originalItems = files;

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
        File parentDirectory = currentDirectory.getParentFile();
        openDirectory(parentDirectory);
    }
}

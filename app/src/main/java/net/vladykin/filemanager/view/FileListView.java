package net.vladykin.filemanager.view;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.List;

/**
 * Interface for class, which are able to show list of
 * files in specific directory.
 *
 * @author Vladimir Vladykin.
 */
public interface FileListView {

    /**
     * Methods, which sets view to different states.
     */
    void showLoading();
    void showFileList(List<FileItem> files);
    void showEmptyView();

    /**
     * Methods for updated part of list.
     */
    void updateItem(int position);
    void removeItem(int position);

    /**
     * Shows or hides back button.
     */
    void setBackButtonVisible(boolean visible);

    /**
     * Tries to onOpen file.
     * View is able to onOpen only the specific file,
     * not directory.
     *
     * Class, which interacts with FileListView,
     * should handle opening directory itself.
     */
    void openFile(File file);

    /**
     * Should show short time error message.
     */
    void showError(String message, Throwable error);

    void setSearchKey(CharSequence searchKey);

    /**
     * Shows to user possible actions with file.
     */
    void showFileActionsUi(FileItem fileItem);

    void showRenameUi(FileItem fileItem);

    void showCreateFileUi(boolean forDirectory);
}

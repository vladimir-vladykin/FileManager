package net.vladykin.filemanager.view;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Interface for class, which are able to show list of
 * files in specific directory.
 *
 * @author Vladimir Vladykin.
 */
public interface FileListView {

    int FILE_OPERATIONS_NOT_SUPPORTED = 1;

    @IntDef(value = {
            FILE_OPERATIONS_NOT_SUPPORTED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ErrorCode {}

    /**
     * Methods, which sets view to different states.
     */
    void showLoading();
    void showFileList(List<FileItem> files);
    void showEmptyView();

    /**
     * Methods for update part of list.
     */
    void insertItem(int position);
    void updateItem(int position);
    void removeItem(int position);
    void moveItem(int from, int to);

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

    void showError(@ErrorCode int errorCode, @Nullable Throwable cause);

    /**
     * Shows to user possible actions with file.
     */
    void showFileActionsUi(FileItem fileItem);

    void showRenameUi(FileItem fileItem);

    void showCreateFileUi(boolean forDirectory);

    void setInsertFileUiActive(boolean active);
}

package net.vladykin.filemanager.util;

import net.vladykin.filemanager.entity.FileItem;

/**
 * Callbacks for file actions.
 *
 * @author Vladimir Vladykin.
 */
public interface FileActionsCallbacks {

    void onOpen(FileItem fileItem);
    void onCopy(FileItem fileItem);
    void onMove(FileItem fileItem);
    void onRename(FileItem fileItem);
    void onRemove(FileItem fileItem);
}

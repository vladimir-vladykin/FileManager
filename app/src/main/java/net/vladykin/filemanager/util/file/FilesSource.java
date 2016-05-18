package net.vladykin.filemanager.util.file;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Interface for classes, which will be root for files in
 * FileListView.
 *
 * @see net.vladykin.filemanager.view.FileListView
 * @author Vladimir Vladykin
 */
public interface FilesSource extends Serializable {

    File getRootDirectory();
    void setCurrentDirectory(File item);
    boolean isRootDirectory(File item);
    String title();
    List<FileItem> getFileList();
}

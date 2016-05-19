package net.vladykin.filemanager.model.source;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Interface for classes, which will be root for files in
 * FileListView.
 * TODO sources with context cannot be serialized correctly
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

package net.vladykin.filemanager.view;

import net.vladykin.filemanager.entity.FileSource;

import java.util.List;

/**
 * View, which let user to choose source for files.
 *
 * @author Vladimir Vladykin
 */
public interface FilesSourcesView {

    void setFilesSources(List<FileSource> sources);
    void showFilesHierarchy(FileSource source);
}

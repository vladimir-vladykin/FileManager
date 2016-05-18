package net.vladykin.filemanager.view;

import net.vladykin.filemanager.entity.FileSourceItem;

import java.util.List;

/**
 * View, which let user to choose source for files.
 *
 * @author Vladimir Vladykin
 */
public interface FilesSourcesView {

    void setFilesSources(List<FileSourceItem> sources);
    void showFilesHierarchy(FileSourceItem source);
}

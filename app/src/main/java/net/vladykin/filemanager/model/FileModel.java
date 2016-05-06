package net.vladykin.filemanager.model;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.List;

import rx.Single;

/**
 * Model for load list of files from specific directory.
 *
 * @author Vladimir Vladykin.
 */
public interface FileModel {

    Single<List<FileItem>> getFiles(File directory);
}

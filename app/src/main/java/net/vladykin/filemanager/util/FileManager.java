package net.vladykin.filemanager.util;

import java.io.File;

import rx.Single;

/**
 * Interface for classes, which do some actions with Files
 * (move, onRename, onRemove)
 *
 * @author Vladimir Vladykin.
 */
public interface FileManager {

    Single<File> copy(File from, File to);
    Single<File> move(File from, File to);
    Single<File> rename(File file, String newFileName);
    Single<Void> remove(File file);
    Single<File> createFile(File directory, String fileName);
    Single<File> createDirectory(File parentDirectory, String directoryName);
}

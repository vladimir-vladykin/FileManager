package net.vladykin.filemanager.util;

import android.util.Log;

import net.vladykin.filemanager.FileManagerApp;
import net.vladykin.filemanager.R;

import java.io.File;
import java.io.IOException;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Class for work with files on local storage.
 *
 * @author Vladimir Vladykin.
 */
public final class LocalFileManager implements FileManager {

    @Override
    public Single<File> copy(File from, File to) {
        return null;
    }

    @Override
    public Single<File> move(File from, File to) {
        return null;
    }

    @Override
    public Single<File> rename(File file, final String newFileName) {
        return Single.just(file)
                .flatMap(oldFile -> {
                    if (FileUtils.renameFile(oldFile, newFileName)) {
                        // return the same instance, but it already has another name
                        return Single.just(oldFile);
                    }

                    String message = FileManagerApp.instance().getApplicationContext()
                            .getString(R.string.file_cannot_rename);
                    return Single.error(new Throwable(message));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Void> remove(File file) {
        Log.d("manager", "remove");
        return null;
    }

    @Override
    public Single<File> createFile(File directory, String fileName) {
        return Single.just(fileName)
                .flatMap(s -> Single.just(
                        createNewFileInternal(directory, fileName))
                );
    }

    @Override
    public Single<File> createDirectory(File parentDirectory, String directoryName) {
        return Single.just(directoryName)
                .flatMap(s -> Single.just(
                        createNewDirectoryInternal(parentDirectory, directoryName)
                ));
    }

    private File createNewFileInternal(File parentDirectory, String fileName) {
        File newFile = new File(parentDirectory, fileName);
        if (newFile.exists()) {
            throw new IllegalStateException("File is already exists");
        }
        try {
            boolean ignored = newFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot create file with path " + newFile.getAbsolutePath(), e);
        }

        return newFile;
    }

    private File createNewDirectoryInternal(File parentDirectory, String directoryName) {
        File newDirectory = new File(parentDirectory, directoryName);
        if (newDirectory.exists()) {
            throw new IllegalStateException("Directory is already exists");
        }
        boolean result = newDirectory.mkdirs();
        if (!result) {
            throw new RuntimeException(
                    "Cannot create directory with path " + newDirectory.getAbsolutePath());
        }

        return newDirectory;
    }
}

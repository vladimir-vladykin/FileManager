package net.vladykin.filemanager.util;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.FileManagerApp;
import net.vladykin.filemanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        return Single
                .fromCallable(() -> {
                    copyFile(from, to);
                    return to;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<File> move(File from, File to) {
        return Single
                .fromCallable(() -> {
                    copyFile(from, to);

                    // is is move, so we have to delete old file
                    deleteFile(from);
                    return to;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<File> rename(File file, final String newFileName) {
        return Single.just(file)
                .flatMap(oldFile -> {
                    File renamedFile = FileUtils.renameFile(oldFile, newFileName);
                    if (renamedFile != null) {
                        // return the same instance, but it already has another name
                        return Single.just(renamedFile);
                    }

                    String message = FileManagerApp.instance().getApplicationContext()
                            .getString(R.string.file_cannot_rename);
                    return Single.error(new Throwable(message));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<File> remove(File removingFile) {
        return Single.just(removingFile)
                .map(file ->  {
                    deleteFile(file);
                    return file;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return;
            }

            for (File deletingFile : listFiles) {
                deleteFile(deletingFile);
            }
        }

        // if it is directory, it is already empty
        boolean result = file.delete();
        if (!result) {
            throw new RuntimeException("Cannot delete file " + file.getAbsolutePath());
        }
    }

    // todo check is to name formated correctly
    private void copyFile(@NonNull File from, @NonNull File to) throws IOException {
        if (to.exists()) {
            throw new IOException("File " + to.getName() +
                    " is already exists");
        }

        if (from.isDirectory()) {
            boolean ignored = to.mkdirs();

            for (String child : from.list()) {
                copyFile(
                        new File(from, child),
                        new File(to, child)
                );
            }
        } else {
            InputStream in = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);

            byte[] buffer = new byte[1024 * 8];
            int length;
            while ( (length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }
}

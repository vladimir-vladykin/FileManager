package net.vladykin.filemanager.model.source;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Source for load hierarchy of files from {@code rootDirectory}.
 *
 * @author Vladimir Vladykin
 */
public final class FileSystemSource implements FilesSource {

    @NonNull private File rootDirectory;
    @NonNull private String title;

    private File currentDirectory;

    public FileSystemSource(@NonNull File rootDirectory, @NonNull String title) {
        this.rootDirectory = rootDirectory;
        this.title = title;

        currentDirectory = this.rootDirectory;
    }

    @Override @NonNull
    public File getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public void setCurrentDirectory(File file) {
        currentDirectory = file;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public boolean isRootDirectory(File file) {
        return rootDirectory.equals(file);
    }

    @Override
    public List<FileItem> getFileList() {
        File[] files = currentDirectory.listFiles();

        // sometimes we got null instead of empty array even for directory(security directory)
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        List<FileItem> items = new ArrayList<>(files.length);
        for (int i = 0, count = files.length; i < count; i++) {
            FileItem info = new FileItem(files[i]);
            items.add(info);
        }

        return items;
    }
}

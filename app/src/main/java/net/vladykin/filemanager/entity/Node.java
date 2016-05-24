package net.vladykin.filemanager.entity;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Entity for represent node of file system hierarchy.
 *
 * @author Vladimir Vladykin
 */
public final class Node {

    private final String title;
    @Nullable private final File directory;

    public Node(String title, @Nullable File directory) {
        this.title = title;
        this.directory = directory;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public File getDirectory() {
        return directory;
    }
}

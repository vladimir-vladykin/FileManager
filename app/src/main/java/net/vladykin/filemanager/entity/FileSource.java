package net.vladykin.filemanager.entity;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Entity for represent source,
 * from which we can load list of files.
 *
 * @author Vladimir Vladykin
 */
public final class FileSource {

    private final String title;
    private final File rootFile;
    @Nullable private final Uri iconUri;

    public FileSource(String title, File rootFile, @Nullable Uri iconUri) {
        this.title = title;
        this.rootFile = rootFile;
        this.iconUri = iconUri;
    }

    public String getTitle() {
        return title;
    }

    public File getRootFile() {
        return rootFile;
    }

    @Nullable
    public Uri getIconUri() {
        return iconUri;
    }
}
package net.vladykin.filemanager.model;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.util.file.FilesSource;

/**
 * TODO comment
 *
 * @author Vladimir Vladykin
 */
public abstract class BaseFileModel implements FileModel {

    @NonNull protected FilesSource filesSource;

    public BaseFileModel(@NonNull FilesSource filesSource) {
        this.filesSource = filesSource;
    }
}

package net.vladykin.filemanager.util;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.presenter.PresenterScope;
import net.vladykin.filemanager.model.source.FilesSource;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Module for provide object, which necessary for work
 * with files.
 *
 * @author Vladimir Vladykin.
 */
@Module
public class FileModule {

    public static final String STORAGE = "storage";
    public static final String ROOT = "root";

    private FilesSource source;

    public FileModule() {
        // use this constructor, if you don't need to use FilesSource
    }

    public FileModule(FilesSource source) {
        this.source = source;
    }

    @Provides @NonNull @PresenterScope
    public FilesSource provideFilesSource() {
        return source;
    }

    @Provides @Named(STORAGE) @NonNull @PresenterScope
    public File provideStorageDirectory() {
        return FileUtils.getStorageDirectory();
    }

    @Provides @Named(ROOT) @NonNull @PresenterScope
    public File provideRootDirectory() {
        return FileUtils.getRootDirectory();
    }

    @Provides @NonNull @PresenterScope
    public FileManager provideFileManager() {
        return new LocalFileManager();
    }
}

package net.vladykin.filemanager.util;

import android.support.annotation.NonNull;

import java.io.File;

import javax.inject.Singleton;

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

    @Provides @NonNull @Singleton
    public File provideRootDirectory() {
        return FileUtils.getRootDirectory();
    }

    @Provides @NonNull @Singleton
    public FileManager provideFileManager() {
        return new LocalFileManager();
    }
}

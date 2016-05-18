package net.vladykin.filemanager.util;

import android.content.Context;
import android.support.annotation.NonNull;

import net.vladykin.filemanager.util.file.FilesSource;
import net.vladykin.filemanager.util.file.VideosSource;

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
    public FilesSource provideFilesRoot(@NonNull Context context, @NonNull File rootDirectory) {
        return new VideosSource(context, "");

        // todo real title
//        return new FileSystemSource(rootDirectory, "");
    }

    @Provides @NonNull @Singleton
    public File provideRootDirectory() {
        return FileUtils.getRootDirectory();
    }

    @Provides @NonNull @Singleton
    public FileManager provideFileManager() {
        return new LocalFileManager();
    }
}

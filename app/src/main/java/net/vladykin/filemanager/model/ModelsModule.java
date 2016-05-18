package net.vladykin.filemanager.model;

import android.content.Context;
import android.support.annotation.NonNull;

import net.vladykin.filemanager.util.file.FilesSource;

import java.io.File;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for provide models.
 *
 * @author Vladimir Vladykin.
 */
@Module
public class ModelsModule {

    @NonNull private File storageDirectory;
    @NonNull private File rootDirectory;

    public ModelsModule(@NonNull File storageDirectory, @NonNull File rootDirectory) {
        this.storageDirectory = storageDirectory;
        this.rootDirectory = rootDirectory;
    }

    @Provides @NonNull
    public FileModel provideFilesModel(FilesSource root) {
        return new LocalFileModel(root);
    }

    @Provides @NonNull
    public FilesSourcesModel provideFileSourcesModel(@NonNull Context context) {
        return new LocalSourcesModel(context, rootDirectory, storageDirectory);
    }
}

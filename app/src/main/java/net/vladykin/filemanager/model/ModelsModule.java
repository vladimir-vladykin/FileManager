package net.vladykin.filemanager.model;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for provide models.
 *
 * @author Vladimir Vladykin.
 */
@Module
public class ModelsModule {

    @Provides @NonNull
    public FileModel provideFilesModel() {
        return new LocalFileModelImpl();
    }

    @Provides @NonNull
    public FilesSourcesModel provideFileSourcesModel() {
        return new FileSourcesModelImpl();
    }
}

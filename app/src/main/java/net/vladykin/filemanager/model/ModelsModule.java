package net.vladykin.filemanager.model;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.util.file.FilesSource;

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
    public FileModel provideFilesModel(FilesSource root) {
        return new LocalFileModel(root);
    }

    @Provides @NonNull
    public FilesSourcesModel provideFileSourcesModel() {
        return new FileSourcesModelImpl();
    }
}

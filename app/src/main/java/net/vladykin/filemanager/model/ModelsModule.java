package net.vladykin.filemanager.model;

import android.content.Context;
import android.support.annotation.NonNull;

import net.vladykin.filemanager.PresenterScope;
import net.vladykin.filemanager.util.file.FilesSource;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static net.vladykin.filemanager.util.FileModule.ROOT;
import static net.vladykin.filemanager.util.FileModule.STORAGE;

/**
 * Dagger module for provide models.
 *
 * @author Vladimir Vladykin.
 */
@Module
public class ModelsModule {

    @Provides @NonNull @PresenterScope
    public FileModel provideFilesModel(FilesSource root) {
        return new LocalFileModel(root);
    }

    @Provides @NonNull @PresenterScope
    public FilesSourcesModel provideFileSourcesModel(@NonNull Context context,
                                                     @NonNull @Named(ROOT) File rootDirectory,
                                                     @NonNull @Named(STORAGE) File storageDirectory) {
        return new LocalSourcesModel(context, rootDirectory, storageDirectory);
    }
}

package net.vladykin.filemanager.presenter;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.model.FileModel;
import net.vladykin.filemanager.model.FilesSourcesModel;
import net.vladykin.filemanager.util.FileManager;
import net.vladykin.filemanager.util.file.FilesSource;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for provide Presenter instances.
 *
 * @author Vladimir Vladykin.
 */
@Module
public class PresenterModule {

    @Provides @NonNull
    public FileListPresenter provideFileListPresenter(@NonNull FileModel fileModel,
                                                      @NonNull FileManager fileManager,
                                                      @NonNull FilesSource root
                                                      /*@NonNull File rootDirectory*/) {
        return new FileListPresenter(fileModel, fileManager, root);
    }

    @Provides @NonNull
    public FilesSourcesPresenter provideFilesSourcesPresenter(@NonNull FilesSourcesModel sourcesModel) {
        return new FilesSourcesPresenter(sourcesModel);
    }

}

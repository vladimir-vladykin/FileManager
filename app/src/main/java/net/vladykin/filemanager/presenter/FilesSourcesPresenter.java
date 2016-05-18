package net.vladykin.filemanager.presenter;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.entity.FileSourceItem;
import net.vladykin.filemanager.model.FilesSourcesModel;
import net.vladykin.filemanager.view.FilesSourcesView;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for interact with FilesSourcesView.
 *
 * @see FilesSourcesView
 * @author Vladimir Vladykin
 */
public final class FilesSourcesPresenter extends Presenter<FilesSourcesView> {

    @NonNull
    private final FilesSourcesModel model;
    private final List<FileSourceItem> sources;

    public FilesSourcesPresenter(@NonNull FilesSourcesModel model) {
        this.model = model;
        sources = new ArrayList<>();
    }

    @Override
    public void bindView(@NonNull FilesSourcesView view) {
        super.bindView(view);

        if (shouldPrepareSources()) {
            prepareSourcesIfNecessary();
        } else {
            view.setFilesSources(sources);
        }
    }

    private void prepareSourcesIfNecessary() {
        model.prepareFilesSources()
                .subscribe(
                        filesSources -> {
                            sources.clear();
                            sources.addAll(filesSources);
                            view().setFilesSources(sources);
                        } // todo error handling here
                );
    }

    private boolean shouldPrepareSources() {
        return sources.isEmpty();
    }
}

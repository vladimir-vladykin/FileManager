package net.vladykin.filemanager.model;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.entity.FileSourceItem;

import java.util.List;

import rx.Single;

/**
 * Model for load list of file sources.
 *
 * @author Vladimir Vladykin
 */
public interface FilesSourcesModel {

    @NonNull
    Single<List<FileSourceItem>> prepareFilesSources();
}

package net.vladykin.filemanager.model;

import android.content.Context;
import android.support.annotation.NonNull;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.FileSourceItem;
import net.vladykin.filemanager.util.file.AudioSource;
import net.vladykin.filemanager.util.file.FileSystemSource;
import net.vladykin.filemanager.util.file.ImagesSource;
import net.vladykin.filemanager.util.file.VideosSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Single;

/**
 * Model for load list of file sources.
 *
 * @author Vladimir Vladykin
 */
public final class LocalSourcesModel implements FilesSourcesModel {

    @NonNull private Context context;
    @NonNull private File rootDirectory;
    @NonNull private File localStorageDirectory;

    @Inject
    public LocalSourcesModel(@NonNull Context context,
                             @NonNull File rootDirectory,
                             @NonNull File localStorageDirectory) {
        this.context = context;
        this.rootDirectory = rootDirectory;
        this.localStorageDirectory = localStorageDirectory;
    }

    @NonNull
    @Override
    public Single<List<FileSourceItem>> prepareFilesSources() {
        // we prepare list synchronously here, because
        // we don't perform any i/o operations here
        List<FileSourceItem> items = new ArrayList<>();

        items.add(imagesItem(context));
        items.add(audioItem(context));
        items.add(videoItem(context));
        items.add(localStorageItem(context, localStorageDirectory));
        items.add(rootItem(context, rootDirectory));

        return Single.just(items);
    }

    private FileSourceItem imagesItem(Context context) {
        String title = context.getString(R.string.file_source_images_title);
        return new FileSourceItem(
                title, R.drawable.ic_images,
                new ImagesSource(context, title)
        );
    }

    private FileSourceItem audioItem(Context context) {
        String title = context.getString(R.string.file_source_audio_title);
        return new FileSourceItem(
                title, R.drawable.ic_audio,
                new AudioSource(context, title)
        );
    }

    private FileSourceItem videoItem(Context context) {
        String title = context.getString(R.string.file_source_video_title);
        return new FileSourceItem(
                title, R.drawable.ic_video,
                new VideosSource(context, title)
        );
    }

    private FileSourceItem localStorageItem(Context context, File localStorageDirectory) {
        String title = context.getString(R.string.file_source_storage_title);
        return new FileSourceItem(
                title, R.drawable.ic_storage,
                new FileSystemSource(localStorageDirectory, title)
        );
    }

    private FileSourceItem rootItem(Context context, File rootDirectory) {
        String title = context.getString(R.string.file_source_root_title);
        return new FileSourceItem(
                title, R.drawable.ic_storage,
                new FileSystemSource(rootDirectory, title)
        );
    }
}

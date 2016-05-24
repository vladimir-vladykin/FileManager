package net.vladykin.filemanager.model.source;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.vladykin.filemanager.BaseApp;
import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Source for load all videos from device.
 *
 * @author Vladimir Vladykin
 */
public final class VideosSource implements FilesSource {

    private static final String[] VIDEO_PROJECTION = new String[] {
            MediaStore.Video.Media.DATA
    };

    // fixme currently cannot keep context because source is serializable
//    @NonNull private Context context;
    @NonNull private String title;

    @Inject
    public VideosSource(/*@NonNull Context context, */@NonNull String title) {
//        this.context = context;
        this.title = title;
    }

    @Override
    public List<FileItem> getFileList() {
        Cursor videoCursor = prepareVideoCursor(BaseApp.instance().getApplicationContext());
        if (videoCursor == null || !videoCursor.moveToNext()) {
            return Collections.emptyList();
        }

        int columnIndexData = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
        List<FileItem> items = new ArrayList<>();
        do {
            String filePath = videoCursor.getString(columnIndexData);
            File file = new File(filePath);
            items.add(new FileItem(file));
        } while (videoCursor.moveToNext());

        return items;
    }

    @Override
    public File getRootDirectory() {
        return null;
    }

    @Override
    public void setCurrentDirectory(File item) {}

    @Nullable
    @Override
    public File getCurrentDirectory() {
        return null;
    }

    @Override
    public boolean isRootDirectory(File item) {
        return true;
    }

    @Override
    public String title() {
        return title;
    }

    private Cursor prepareVideoCursor(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        return context.getContentResolver().query(
                uri, VIDEO_PROJECTION, null, null, null);
    }
}

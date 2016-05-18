package net.vladykin.filemanager.util.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Source for load all images from device.
 *
 * @author Vladimir Vladykin
 */
public final class ImagesSource implements FilesSource {

    @NonNull private Context context;
    @NonNull private String title;

    @Inject
    public ImagesSource(@NonNull Context context, @NonNull String title) {
        this.context = context;
        this.title = title;
    }

    @Override
    public File getRootDirectory() {
        return null;
    }

    @Override
    public void setCurrentDirectory(File item) {}

    @Override
    public boolean isRootDirectory(File item) {
        // since images list won't have directories, we return always true
        return true;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public List<FileItem> getFileList() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA};

        Cursor cursor = context.getContentResolver().
                query(uri, projection, null, null, null);
        if (cursor == null) {
             return Collections.emptyList();
        }

        List<FileItem> items = new ArrayList<>();
        int columnIndexData = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            String pathToFile = cursor.getString(columnIndexData);
            FileItem item = createFileItem(pathToFile);
            items.add(item);
        }

        cursor.close();
        return items;
    }

    private FileItem createFileItem(String pathToFile) {
        File file = new File(pathToFile);
        return new FileItem(file);
    }
}

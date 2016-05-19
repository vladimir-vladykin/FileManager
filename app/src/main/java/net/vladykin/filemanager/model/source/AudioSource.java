package net.vladykin.filemanager.model.source;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import net.vladykin.filemanager.BaseApp;
import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Source for load all audio files from device storage.
 *
 * @author Vladimir Vladykin
 */
public final class AudioSource implements FilesSource {

    private static final String[] AUDIO_PROJECTION = new String[] {
            MediaStore.Audio.Media.DATA
    };

//    @NonNull private Context context;
    @NonNull private String title;

    @Inject
    public AudioSource(/*@NonNull Context context, */@NonNull String title) {
//        this.context = context;
        this.title = title;
    }

    @Override
    public List<FileItem> getFileList() {
        Cursor audioCursor = prepareAudioCursor(BaseApp.instance().getApplicationContext());
        if (audioCursor == null || !audioCursor.moveToFirst()) {
            return Collections.emptyList();
        }

        int columnIndexData = audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        List<FileItem> items = new ArrayList<>();
        do {
            String filePath = audioCursor.getString(columnIndexData);
            File file = new File(filePath);
            items.add(new FileItem(file));
        } while (audioCursor.moveToNext());

        return items;
    }

    @Override
    public File getRootDirectory() {
        return null;
    }

    @Override
    public void setCurrentDirectory(File item) {}

    @Override
    public boolean isRootDirectory(File item) {
        // todo comment
        return true;
    }

    @Override
    public String title() {
        return title;
    }

    private Cursor prepareAudioCursor(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        return context.getContentResolver().query(
                uri, AUDIO_PROJECTION, selection, null, null);
    }
}

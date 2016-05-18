package net.vladykin.filemanager.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Владимир on 22.02.2015.
 *
 * Class, which contains some methods for easy work with files.
 */
public class FileUtils {


    public static File getStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }
    public static File getRootDirectory() {
        return Environment.getRootDirectory();
    }

    public static boolean isRootDirectory(File directory) {
        return getRootDirectory().equals(directory);
    }

    public static boolean openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        //get file type
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String fileName = file.getName();
        String format = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
        String mimeType = mime.getMimeTypeFromExtension(format);

        //if we don't know, what file it is
        if(mimeType == null) {
            return false;
        }

        intent.setDataAndType(Uri.fromFile(file), mimeType);

        //check does system have app for onOpen this file
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean hasApp = activities.size() > 0;

        //if system has app
        if(hasApp) {
            //start it
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    // TODO asynchronously
    public static void deleteFile(File file, CompleteListener listener) {
        if (file.isDirectory()) {
            deleteFile(file, null);
        } else {
            boolean result = file.delete();

            // if it is top method
            if (listener != null) {

                // if we've successfully deleted file
                if (result) {
                    // notify about success
                    listener.onComplete();
                } else {
                    listener.onFailure();
                }
            }

        }
    }

    public static boolean createFile(File directory, String fileName) {
        File newFile = new File(directory, fileName);
        try {
            return newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if we've caught exception, return false
        return false;
    }

    public static boolean createDirectory(File parentDirectory, String directoryName) {
        File newDirectory = new File(parentDirectory, directoryName);
        return newDirectory.mkdir();
    }

    @Nullable
    public static File renameFile(File file, String newName) {
        File parentDirectory = file.getParentFile();
        File newFile = new File(parentDirectory, newName);
        boolean renamed = file.renameTo(newFile);
        if (!renamed) {
            return null;
        }

        return newFile;
    }

    public interface CompleteListener {
        void onComplete();
        void onFailure();
    }
}

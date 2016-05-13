package net.vladykin.filemanager.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Class for help work with runtime permissions.
 *
 * @author Vladimir Vladykin
 */
public final class Permissions {

    public static final int READ_EXTERNAL_STORAGE_CODE = 123;

    public static boolean isReadExternalStorageAllowed(Context context) {
        return isGrantedPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean shouldShowStorageRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
        );
    }

    /**
     * todo remove
     */
    @Deprecated
    public static void requestReadExternalStorage(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_CODE
        );
    }

    public static void requestReadExternalStorage(Fragment fragment) {
        fragment.requestPermissions(
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_CODE
        );
    }

    private static boolean isGrantedPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private Permissions() {
        throw new UnsupportedOperationException();
    }
}

package net.vladykin.filemanager.util;

import android.content.Context;
import android.net.Uri;

/**
 * Class for help format Uri.
 *
 * @author Vladimir Vladykin
 */
public final class UriUtils {

    private static final String SLASH = "/";
    private static final String ANDROID_RESOURCE = "android.resource://";

    public static Uri resourceIdToUri(Context context, int resId) {
        return Uri.parse(ANDROID_RESOURCE
                + SLASH + context.getPackageName()
                + SLASH + resId
        );
    }

    private UriUtils() {
        throw new UnsupportedOperationException();
    }
}

package net.vladykin.filemanager.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.FileItem;

/**
 * Class for help instantiate dialog with file actions.
 *
 * @author Vladimir Vladykin.
 */
public final class FileActions {

    private static final int OPEN = 0, COPY = 1,
            MOVE = 2, RENAME = 3, REMOVE = 4;

    public static MaterialDialog.Builder prepareDialog(@NonNull Context context,
                                                       @NonNull final FileItem fileItem,
                                                       @NonNull final FileActionsCallbacks callbacks) {
        return new MaterialDialog.Builder(context)
                .items(R.array.file_dialog_actions)
                .itemsCallback((dialog, itemView, which, text) -> {
                    invokeNecessaryCallback(which, fileItem, callbacks);
                });
    }

    private static void invokeNecessaryCallback(int which, FileItem fileItem, FileActionsCallbacks callbacks) {
        switch (which) {
            case OPEN:
                callbacks.onOpen(fileItem);
                break;
            case COPY:
                callbacks.onCopy(fileItem);
                break;
            case MOVE:
                callbacks.onMove(fileItem);
                break;
            case RENAME:
                callbacks.onRename(fileItem);
                break;
            case REMOVE:
                callbacks.onRemove(fileItem);
        }
    }
}

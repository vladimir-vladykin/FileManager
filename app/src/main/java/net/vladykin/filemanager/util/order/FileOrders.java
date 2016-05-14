package net.vladykin.filemanager.util.order;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

import static net.vladykin.filemanager.util.order.FileItemComparators.alphabet;
import static net.vladykin.filemanager.util.order.FileItemComparators.lastModified;
import static net.vladykin.filemanager.util.order.FileItemComparators.reversedAlphabet;
import static net.vladykin.filemanager.util.order.FileItemComparators.reversedSize;
import static net.vladykin.filemanager.util.order.FileItemComparators.size;

/**
 * Class for help choose comparators for FileItems.
 *
 * @author Vladimir Vladykin
 */
public final class FileOrders {

    private static final int ALPHABET = 0, REVERSED_ALPHABET = 1, SIZE = 2,
            REVERSED_SIZE = 3, LAST_MODIFIED = 4;

    public static MaterialDialog.Builder prepareDialog(@NonNull Context context,
                                                       @NonNull FileOrdersCallback callback) {
        return new MaterialDialog.Builder(context)
                .title(R.string.action_sort)
                .items(R.array.file_orders)
                .itemsCallback((dialog, itemView, which, text) ->
                        invokeCallbackWithComparator(which, callback));
    }

    private static void invokeCallbackWithComparator(int chosenPosition, FileOrdersCallback callback) {
        Comparator<FileItem> comparator = null;
        switch (chosenPosition) {
            case ALPHABET:
                comparator = alphabet();
                break;
            case REVERSED_ALPHABET:
                comparator = reversedAlphabet();
                break;
            case SIZE:
                comparator = size();
                break;
            case REVERSED_SIZE:
                comparator = reversedSize();
                break;
            case LAST_MODIFIED:
                comparator = lastModified();
                break;
            default:
                Log.w("FileOrders", "Cannot understand what order should be used for items");
        }

        if (comparator != null) {
            callback.onOrderChosen(comparator);
        }
    }

    private FileOrders() {
        throw new UnsupportedOperationException();
    }
}

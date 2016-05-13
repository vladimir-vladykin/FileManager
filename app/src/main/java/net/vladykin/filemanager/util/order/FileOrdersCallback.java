package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Interface for classes which listen order choosing.
 *
 * @author Vladimir Vladykin
 */
public interface FileOrdersCallback {

    void onOrderChosen(Comparator<FileItem> comparator);
}

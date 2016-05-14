package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Comparator for sort FileItems by last modified date.
 *
 * @see FileItem
 * @author Vladimir Vladykin
 */
public final class LastModifiedComparator implements Comparator<FileItem> {

    @Override
    public int compare(FileItem left, FileItem right) {
        long leftTime = left.getLastModified();
        long rightTime = right.getLastModified();
        return leftTime == rightTime ? 0 : leftTime < rightTime ? 1 : -1;
    }
}

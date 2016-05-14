package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Just like AlphabetComparator, but sorts items in reversed order.
 *
 * @see AlphabetComparator
 * @see FileItem
 * @author Vladimir Vladykin
 */
public final class ReversedAlphabetComparator implements Comparator<FileItem> {

    @Override
    public int compare(FileItem first, FileItem second) {
        return -1 * first.getName().compareTo(second.getName());
    }
}

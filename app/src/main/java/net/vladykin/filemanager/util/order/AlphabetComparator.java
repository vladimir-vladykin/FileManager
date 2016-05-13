package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Sorts FileItems by alphabet.
 *
 * @see FileItem
 * @author Vladimir Vladykin
 */
public final class AlphabetComparator implements Comparator<FileItem> {

    @Override
    public int compare(FileItem first, FileItem second) {
        return first.getName().compareTo(second.getName());
    }
}

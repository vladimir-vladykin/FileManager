package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Class for instantiate Comparators for sort FileItems.
 *
 * @see net.vladykin.filemanager.entity.FileItem
 * @author Vladimir Vladykin
 */
public final class FileItemComparators {

    private static final AlphabetComparator ALPHABET_COMPARATOR = new AlphabetComparator();
    private static final SizeComparator SIZE_COMPARATOR = new SizeComparator();

    public static Comparator<FileItem> alphabet() {
        return ALPHABET_COMPARATOR;
    }

    public static Comparator<FileItem> size() {
        return SIZE_COMPARATOR;
    }

    private FileItemComparators() {
        throw new UnsupportedOperationException();
    }
}

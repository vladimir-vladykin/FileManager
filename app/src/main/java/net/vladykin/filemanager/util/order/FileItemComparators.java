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
    private static final ReversedAlphabetComparator REVERSED_ALPHABET_COMPARATOR = new ReversedAlphabetComparator();
    private static final SizeComparator SIZE_COMPARATOR = new SizeComparator();
    private static final ReversedSizeComparator REVERSED_SIZE_COMPARATOR = new ReversedSizeComparator();
    private static final LastModifiedComparator LAST_MODIFIED_COMPARATOR = new LastModifiedComparator();

    public static Comparator<FileItem> alphabet() {
        return ALPHABET_COMPARATOR;
    }

    public static Comparator<FileItem> reversedAlphabet() {
        return REVERSED_ALPHABET_COMPARATOR;
    }

    public static Comparator<FileItem> size() {
        return SIZE_COMPARATOR;
    }

    public static Comparator<FileItem> reversedSize() {
        return REVERSED_SIZE_COMPARATOR;
    }

    public static Comparator<FileItem> lastModified() {
        return LAST_MODIFIED_COMPARATOR;
    }

    private FileItemComparators() {
        throw new UnsupportedOperationException();
    }
}

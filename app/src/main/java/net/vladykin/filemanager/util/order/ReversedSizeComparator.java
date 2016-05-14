package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Just like SizeComparator, but sorts items in reversed order.
 * Directories still above files.
 *
 * @see FileItem
 * @see SizeComparator
 * @author Vladimir Vladykin
 */
public final class ReversedSizeComparator implements Comparator<FileItem> {

    @Override
    public int compare(FileItem left, FileItem right) {
        return left.isDirectory() || right.isDirectory()
                ? compareDirectories(left, right) : compareFiles(left, right);
    }

    private int compareDirectories(FileItem left, FileItem right) {
        if (left.isDirectory() && right.isDirectory()) {
            return right.getChildFilesCount() - left.getChildFilesCount();
        }

        // only one file is directory, we should tell that directory is bigger.
        // here no difference with SizeComparator
        return left.isDirectory() ? 1 : -1;
    }

    private int compareFiles(FileItem left, FileItem right) {
        return (int) (right.getSize() - left.getSize());
    }


}

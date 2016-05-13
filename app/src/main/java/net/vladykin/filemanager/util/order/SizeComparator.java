package net.vladykin.filemanager.util.order;

import net.vladykin.filemanager.entity.FileItem;

import java.util.Comparator;

/**
 * Sorts FileItems by size (file size or directory child files count).
 * Directories is first, files after them.
 *
 * @author Vladimir Vladykin
 */
public final class SizeComparator implements Comparator<FileItem> {

    @Override
    public int compare(FileItem left, FileItem right) {
        return left.isDirectory() || right.isDirectory()
                ? compareDirectories(left, right) : compareFiles(left, right);
    }

    private int compareDirectories(FileItem left, FileItem right) {
        if (left.isDirectory() && right.isDirectory()) {
            return left.getChildFilesCount() - right.getChildFilesCount();
        }

        // only one file is directory, we should tell that directory is bigger
        return left.isDirectory() ? 1 : -1;
    }

    private int compareFiles(FileItem left, FileItem right) {
        return (int) (left.getSize() - right.getSize());
    }
}

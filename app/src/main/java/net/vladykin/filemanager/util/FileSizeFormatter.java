package net.vladykin.filemanager.util;

import java.text.DecimalFormat;

/**
 * Class for help format file or directory sizes
 *
 * @author Vladimir Vladykin
 */
public final class FileSizeFormatter {

    // todo from strings.xml
    /**
     * Up to Long.MAX_VALUE
     */
    private static final String[] UNITS =
            new String[] {"b", "kb", "mb", "gb", "tb", "pb", "eb"};
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.#");

    public static String format(long fileSize) {
        if (fileSize <= 0) {
            return "0 " + UNITS[0];
        }

        final int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));
        return FORMATTER.format(fileSize / Math.pow(1024, digitGroups)) + " " + UNITS[digitGroups];
    }

    private FileSizeFormatter() {
        throw new UnsupportedOperationException();
    }
}

package net.vladykin.filemanager.entity;

import net.vladykin.filemanager.model.source.FilesSource;

/**
 * Entity for represent source,
 * from which we can load list of files.
 *
 * @author Vladimir Vladykin
 */
public final class FileSourceItem {

    private final String title;
    private final int iconId;
    private final FilesSource source;

    public FileSourceItem(String title, int iconId, FilesSource source) {
        this.title = title;
        this.iconId = iconId;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public int getIconId() {
        return iconId;
    }

    public FilesSource getSource() {
        return source;
    }
}

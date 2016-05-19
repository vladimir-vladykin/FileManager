package net.vladykin.filemanager;

import android.support.annotation.Nullable;

/**
 * Interface for manage toolbar state
 *
 * @author Vladimir Vladykin
 */
public interface ToolbarController {

    void setToolbarTitle(@Nullable CharSequence title);
}

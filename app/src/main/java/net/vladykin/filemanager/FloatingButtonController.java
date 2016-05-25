package net.vladykin.filemanager;

import android.view.View;

/**
 * Interface for manage state of FlotingActionButton.
 *
 * @author Vladimir Vladykin
 */
public interface FloatingButtonController {

    void showActionButton(View.OnClickListener clickListener);
    void hideActionButton();
}

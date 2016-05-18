package net.vladykin.filemanager.navigation;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Base class for handle navigation within application.
 *
 * @author Vladimir Vladykin
 */
public abstract class BaseRouter {

    @NonNull
    private final FragmentManager fragmentManager;
    private final int containerId;

    public BaseRouter(@NonNull FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    protected void startFragment(final Fragment fragment) {
        final String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}

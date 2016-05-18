package net.vladykin.filemanager.navigation;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import net.vladykin.filemanager.fragment.FileListFragment;
import net.vladykin.filemanager.fragment.FileSourcesFragment;
import net.vladykin.filemanager.util.file.FilesSource;

/**
 * Class for handle navigation within application.
 *
 * @author Vladimir Vladykin
 */
public final class MainRouter extends BaseRouter {

    public MainRouter(@NonNull FragmentManager fragmentManager, int containerId) {
        super(fragmentManager, containerId);
    }

    public void onAppStarted() {
        startFragment(new FileSourcesFragment());
    }

    public void onFileSourceChosen(FilesSource source) {
        startFragment(FileListFragment.newInstance(source));
    }
}

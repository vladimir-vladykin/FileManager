package net.vladykin.filemanager.presenter;

import net.vladykin.filemanager.fragment.FileListFragment;
import net.vladykin.filemanager.fragment.FileSourcesFragment;
import net.vladykin.filemanager.model.ModelsModule;
import net.vladykin.filemanager.util.FileModule;

import dagger.Subcomponent;

/**
 * Subcomponent for inject presenters with all needed objects.
 *
 * @author Vladimir Vladykin
 */
@PresenterScope
@Subcomponent(modules = {
        FileModule.class,
        ModelsModule.class,
        PresenterModule.class
})
public interface PresenterSubComponent {

    void inject(FileListFragment fragment);
    void inject(FileSourcesFragment fragment);
}

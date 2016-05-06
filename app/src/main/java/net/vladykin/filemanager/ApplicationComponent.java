package net.vladykin.filemanager;

import net.vladykin.filemanager.fragment.FileListFragment;
import net.vladykin.filemanager.model.ModelsModule;
import net.vladykin.filemanager.presenter.PresenterModule;
import net.vladykin.filemanager.util.FileModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        FileModule.class,
        ModelsModule.class,
        PresenterModule.class
})
public interface ApplicationComponent {

    void inject(FileListFragment fragment);
}
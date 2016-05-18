package net.vladykin.filemanager;

import net.vladykin.filemanager.presenter.PresenterSubComponent;
import net.vladykin.filemanager.util.FileModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {

    PresenterSubComponent addPresenterSubComponent(FileModule fileModule);
}

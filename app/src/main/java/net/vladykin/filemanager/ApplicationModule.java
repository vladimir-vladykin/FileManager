package net.vladykin.filemanager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

/**
 * Module for provide context
 *
 * @author Vladimir Vladykin
 */
@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides @NonNull
    public Context provideContext() {
        return application.getApplicationContext();
    }
}

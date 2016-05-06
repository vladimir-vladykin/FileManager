package net.vladykin.filemanager;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * Base class for custom application classes.
 *
 * @author Vladimir Vladykin
 */
public class BaseApp extends Application {

    private static BaseApp instance;
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        applicationComponent = prepareApplicationComponent().build();
    }

    @NonNull
    public static BaseApp instance() {
        return instance;
    }

    @NonNull
    public static ApplicationComponent component() {
        return instance().applicationComponent;
    }

    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return DaggerApplicationComponent.builder();
    }

}

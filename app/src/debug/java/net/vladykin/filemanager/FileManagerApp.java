package net.vladykin.filemanager;

import android.view.Gravity;

import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Application class for debug builds.
 *
 * @author Vladimir Vladykin.
 */
public class FileManagerApp extends BaseFileManagerApp {

    @Override
    public void onCreate() {
        super.onCreate();
        setupTinyDancer();
        setupStetho();
        setupLeakCanary();
    }

    private void setupTinyDancer() {
        TinyDancer.create()
                .redFlagPercentage(0.1f)
                .startingGravity(Gravity.TOP)
                .startingXPosition(0)
                .startingYPosition(0)
                .show(this);
    }

    private void setupStetho() {
        Stetho.initializeWithDefaults(this);
    }

    private void setupLeakCanary() {
        LeakCanary.install(this);
    }
}

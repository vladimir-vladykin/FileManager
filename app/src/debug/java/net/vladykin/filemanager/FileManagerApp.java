package net.vladykin.filemanager;

import android.view.Gravity;

import com.codemonkeylabs.fpslibrary.TinyDancer;

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
    }

    private void setupTinyDancer() {
        TinyDancer.create()
                .redFlagPercentage(0.1f)
                .startingGravity(Gravity.TOP)
                .startingXPosition(200)
                .startingYPosition(600)
                .show(this);
    }
}

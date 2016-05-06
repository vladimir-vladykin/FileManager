package net.vladykin.filemanager.util;

import android.util.Log;

import net.vladykin.filemanager.FileManagerApp;
import net.vladykin.filemanager.R;

import java.io.File;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Class for work with files on local storage.
 *
 * @author Vladimir Vladykin.
 */
public final class LocalFileManager implements FileManager {

    @Override
    public Single<File> copy(File from, File to) {
        return null;
    }

    @Override
    public Single<File> move(File from, File to) {
        return null;
    }

    @Override
    public Single<File> rename(File file, final String newFileName) {
        return Single.just(file)
                .flatMap(oldFile -> {
                    if (FileUtils.renameFile(oldFile, newFileName)) {
                        // return the same instance, but it already has another name
                        return Single.just(oldFile);
                    }

                    String message = FileManagerApp.instance().getApplicationContext()
                            .getString(R.string.file_cannot_rename);
                    return Single.error(new Throwable(message));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Void> remove(File file) {
        Log.d("manager", "remove");
        return null;
    }
}

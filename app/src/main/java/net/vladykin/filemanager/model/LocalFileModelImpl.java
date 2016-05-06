package net.vladykin.filemanager.model;

import net.vladykin.filemanager.entity.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Model for load files list from local storage.
 *
 * @author Vladimir Vladykin.
 */
public final class LocalFileModelImpl implements FileModel {

    @Override
    public Single<List<FileItem>> getFiles(File directory) {
        return Single.just(directory)
                .flatMap(filesDirectory -> Single.just(parseFilesInfo(directory)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<FileItem> parseFilesInfo(File directory) {
        File[] files = directory.listFiles();

        // sometimes we got null instead of empty array even for directory(security directory)
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        List<FileItem> filesInfo = new ArrayList<>(files.length);
        for (int i = 0, count = files.length; i < count; i++) {
            FileItem info = new FileItem(files[i]);
            filesInfo.add(info);
        }

        return filesInfo;
    }
}

package net.vladykin.filemanager.model;

import android.support.annotation.NonNull;

import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.util.file.FilesSource;

import java.util.List;

import javax.inject.Inject;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Model for load files list from local storage.
 *
 * @author Vladimir Vladykin.
 */
public final class LocalFileModel extends BaseFileModel {

    @Inject
    public LocalFileModel(@NonNull FilesSource filesSource) {
        super(filesSource);
    }

    @Override
    public Single<List<FileItem>> getFiles() {
        return Single.just(filesSource)
                .flatMap(root -> Single.just(root.getFileList()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
//
//    private List<FileItem> parseFilesInfo(File directory) {
//        File[] files = directory.listFiles();
//
//        // sometimes we got null instead of empty array even for directory(security directory)
//        if (files == null || files.length == 0) {
//            return Collections.emptyList();
//        }
//
//        List<FileItem> filesInfo = new ArrayList<>(files.length);
//        for (int i = 0, count = files.length; i < count; i++) {
//            FileItem info = new FileItem(files[i]);
//            filesInfo.add(info);
//        }
//
//        return filesInfo;
//    }
}

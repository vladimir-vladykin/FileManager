package net.vladykin.filemanager.util;

import android.os.AsyncTask;

import net.vladykin.filemanager.model.FileInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Владимир on 22.02.2015.
 *
 * Task for create ArrayList of FileInfo objects from File objects asynchronously.
 * Then FileInfo objects will be used as info for files list view items.
 */
public class FileInfoCreatorTask extends AsyncTask<Void, Void, ArrayList<FileInfo>> {

    /**
     * If count of file in directory bigger then this limit,
     * we will think, that it is long operation and show loader.
     */
    private static final int LIMIT_LONG_LOADING = 100;

    private File mDirectory;
    private OnFileInfoCreationListener mListener;

    public FileInfoCreatorTask(File directory, OnFileInfoCreationListener listener) {
        mDirectory = directory;
        mListener = listener;
    }

    @Override
    protected ArrayList<FileInfo> doInBackground(Void... params) {
        ArrayList<FileInfo> filesInfo = new ArrayList<>();

        //get list of child files
        File [] files = mDirectory.listFiles();

        //sometimes we got null instead of empty array even for directory(security directory)
        if(files != null) {
            //fill array of FileInfo objects
            for(File file : files){
                FileInfo info = new FileInfo(file);
                filesInfo.add(info);
            }
        }
        return filesInfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        final String [] list = mDirectory.list();

        //if length of list more than limit, it is long operation
        final boolean isLongOperation = list != null && list.length > LIMIT_LONG_LOADING;
        mListener.onStartCreation(isLongOperation);
    }

    @Override
    protected void onPostExecute(ArrayList<FileInfo> filesInfo) {
        super.onPostExecute(filesInfo);
        mListener.onCreated(filesInfo);
    }

    public interface OnFileInfoCreationListener {
        public void onStartCreation(boolean isLongOperation);
        public void onCreated(ArrayList<FileInfo> filesInfo);
    }
}

package net.vladykin.filemanager.model;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Владимир on 22.02.2015.
 *
 * Class, which contains information about file.
 */
public class FileInfo implements Serializable {
    private String mName;
    private long mSize;
    private long mLastModified;
    private Type mType;
    private File mFile;

    public FileInfo(File file) {
        mFile = file;
        updateFileInfo();
    }


    public void updateFileInfo() {
        mName = mFile.getName();
        //TODO maybe not correct
        mSize = mFile.length();
        mLastModified = mFile.lastModified();

        //TODO different kind of files
        if(mFile.isDirectory()) {
            mType = Type.DIRECTORY;
        } else {
            //unknown file
            mType = Type.UNSPECIFIED;
        }
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public long getSize() {
        return mSize;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public Type getType() {
        return mType;
    }

    public File getFile() {
        return mFile;
    }

    public enum Type {
        UNSPECIFIED, //unknown file
        DIRECTORY
    }
}

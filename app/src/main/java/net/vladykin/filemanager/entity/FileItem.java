package net.vladykin.filemanager.entity;

import java.io.File;
import java.io.Serializable;

/**
 * Entity, which contains information about file.
 *
 * @author Vladimir Vladykin
 */
public final class FileItem implements Serializable {
    private String mName;
    private long mSize;
    private long mLastModified;
    private Type mType;
    private File mFile;

    /**
     * Exists only if mFile is directory.
     */
    private int mChildFilesCount;

    public FileItem(File file) {
        mFile = file;
        updateFileInfo();
    }

    public void updateFileInfo() {
        mName = mFile.getName();
        mSize = mFile.length();
        mLastModified = mFile.lastModified();

        //TODO different kind of files
        boolean fileIsDirectory = mFile.isDirectory();
        if(fileIsDirectory) {
            mType = Type.DIRECTORY;
        } else {
            //unknown file
            mType = Type.UNSPECIFIED;
        }

        if (fileIsDirectory) {
            File[] listFiles = mFile.listFiles();
            mChildFilesCount = listFiles != null ? listFiles.length : 0;
        } else {
            mChildFilesCount = 0;
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

    public int getChildFilesCount() {
        return mChildFilesCount;
    }

    public boolean isDirectory() {
        return mType == Type.DIRECTORY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileItem fileItem = (FileItem) o;

        if (mSize != fileItem.mSize) return false;
        if (mLastModified != fileItem.mLastModified) return false;
        if (mChildFilesCount != fileItem.mChildFilesCount) return false;
        if (mName != null ? !mName.equals(fileItem.mName) : fileItem.mName != null) return false;
        if (mType != fileItem.mType) return false;
        return mFile != null ? mFile.equals(fileItem.mFile) : fileItem.mFile == null;

    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (int) (mSize ^ (mSize >>> 32));
        result = 31 * result + (int) (mLastModified ^ (mLastModified >>> 32));
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        result = 31 * result + (mFile != null ? mFile.hashCode() : 0);
        result = 31 * result + mChildFilesCount;
        return result;
    }

    public enum Type {
        UNSPECIFIED, // unknown file
        DIRECTORY
    }
}

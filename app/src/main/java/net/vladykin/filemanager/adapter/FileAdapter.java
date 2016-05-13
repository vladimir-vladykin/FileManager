package net.vladykin.filemanager.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.util.FileSizeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Владимир on 14.02.2015.
 *
 * Adapter for list of files.
 * Takes care about enter animation, when necessary.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    private static final int ICONS_CACHE_SIZE = 2;

    private Context mContext;
    private List<FileItem> mFilesInfo;

    /**
     * Date formatter for show file updating time correctly.
     */
    private final SimpleDateFormat mFormat
            = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    /**
     * Cache for files or directories icons.
     */
    private LruCache<Integer, Bitmap> mIconsCache;

    public FileAdapter(Context context) {
        mContext = context;
        mIconsCache = new LruCache<>(ICONS_CACHE_SIZE);
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        FileItem fileItem = mFilesInfo.get(position);
        holder.mFileNameView.setText(fileItem.getName());
        setSizeToHolder(holder.mFileSizeView, fileItem);
//        holder.mFileSizeView.setText(FileSizeFormatter.format(fileItem.getSize()));
        holder.mFileLastModifiedView.setText(mFormat.format(fileItem.getLastModified()));

        Bitmap icon = getIconByType(fileItem.getType());
        holder.mImageView.setImageBitmap(icon);
    }

    private void setSizeToHolder(TextView sizeTextView, FileItem fileItem) {
        File file = fileItem.getFile();
        if (file.isDirectory()) {
            Resources res = sizeTextView.getResources();
            final int filesCount = fileItem.getChildFilesCount();
            sizeTextView.setText(res.getQuantityString(R.plurals.file_count, filesCount, filesCount));
        } else {
            sizeTextView.setText(FileSizeFormatter.format(fileItem.getSize()));
        }
    }

    @Override
    public int getItemCount() {
        return mFilesInfo != null ? mFilesInfo.size() : 0;
    }

    public void notifyRemoved(int position) {
        notifyItemRemoved(position);
    }

    private Bitmap getIconByType(final FileItem.Type type) {
        int iconId = 0;
        switch (type) {
            case DIRECTORY:
                iconId = R.drawable.ic_folder;
                break;
            case UNSPECIFIED:
                iconId = R.drawable.ic_file;
                break;
        }
        return getIconFromCache(iconId);
    }

    private Bitmap getIconFromCache(final int id) {
        //try to get icon from cache
        Bitmap icon = mIconsCache.get(id);

        //if we haven't got icon from cache
        if(icon == null) {
            //read this from resources
            icon = BitmapFactory.decodeResource(mContext.getResources(), id);

            //and put it to cache
            mIconsCache.put(id, icon);
        }
        return icon;
    }

    public void setFilesInfo(List<FileItem> filesInfo) {
        mFilesInfo = filesInfo;
        notifyDataSetChanged();
    }

    public FileItem getFileInfo(int position) {
        return mFilesInfo.get(position);
    }

    public static class FileHolder extends RecyclerView.ViewHolder {
        protected ImageView mImageView;
        protected TextView mFileNameView;
        protected TextView mFileSizeView;
        protected TextView mFileLastModifiedView;

        public FileHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.file_image);
            mFileNameView = (TextView) itemView.findViewById(R.id.file_name);
            mFileSizeView = (TextView) itemView.findViewById(R.id.file_size);
            mFileLastModifiedView = (TextView) itemView.findViewById(R.id.file_last_modified);
        }
    }
}

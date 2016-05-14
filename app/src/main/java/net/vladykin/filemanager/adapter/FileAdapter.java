package net.vladykin.filemanager.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.FileItem;
import net.vladykin.filemanager.util.FileSizeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Владимир on 14.02.2015.
 *
 * Adapter for list of files.
 * Takes care about enter animation, when necessary.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder>
        implements Filterable {
    private static final int ICONS_CACHE_SIZE = 2;

    private Context mContext;
    private List<FileItem> mFilesInfo;
    private List<FileItem> mSortedFileInfo;

    private Filter itemsFilter;
    private CharSequence searchKey;

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
        itemsFilter = new ItemsFilter();
        mSortedFileInfo = new ArrayList<>();
    }

    public void filter(CharSequence searchKey) {
        this.searchKey = searchKey;
        performListFiltering();
    }

    @Override
    public Filter getFilter() {
        // todo maybe keep search key inside adapter, because we need to refilter new establishing list
        return itemsFilter;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        FileItem fileItem = mSortedFileInfo.get(position);
        holder.mFileNameView.setText(fileItem.getName());
        setSizeToHolder(holder.mFileSizeView, fileItem);
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
        return mSortedFileInfo.size();
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
        if (TextUtils.isEmpty(searchKey)) {
            mSortedFileInfo.clear();
            mSortedFileInfo.addAll(mFilesInfo);
            notifyDataSetChanged();
        } else {
            performListFiltering();
        }
    }

    public FileItem getFileInfo(int position) {
        return mFilesInfo.get(position);
    }

    private void performListFiltering() {
        getFilter().filter(searchKey);
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

    // fixme filter code is bad; unefficient, unreadable
    private final class ItemsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // todo should we create new every time
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = mFilesInfo;
                results.count = mFilesInfo.size();
            } else {
                // fixme really? we need new every time
                List<FileItem> buffer = new ArrayList<>();

                String upperCasedConstraint = constraint.toString().toUpperCase();
                for (FileItem fileItem : mFilesInfo) {
                    if (fileItem.getName().toUpperCase()
                            .startsWith(upperCasedConstraint)) {
                        buffer.add(fileItem);
                    }
                }

                results.values = buffer;
                results.count = buffer.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSortedFileInfo.clear();
            mSortedFileInfo.addAll((List<FileItem>) results.values);
            notifyDataSetChanged();
        }
    }
}

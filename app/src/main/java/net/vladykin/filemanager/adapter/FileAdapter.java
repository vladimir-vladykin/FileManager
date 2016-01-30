package net.vladykin.filemanager.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.model.FileInfo;
import net.vladykin.filemanager.util.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Владимир on 14.02.2015.
 *
 * Adapter for list of files.
 * Takes care about enter animation, when necessary.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    private static final int ICONS_CACHE_SIZE = 2;
    private static final int ITEM_ANIMATION_DURATION = 600;
    private static final int ANIMATED_ITEM_COUNTS = 6;

    private Context mContext;
    private ArrayList<FileInfo> mFilesInfo;

    /**
     * Date formatter for show file updating time correctly.
     */
    private final SimpleDateFormat mFormat
            = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    /**
     * Cache for files or directories icons.
     */
    private LruCache<Integer, Bitmap> mIconsCache;

    /**
     * For detect which item was animated last.
     */
    private int mLastAnimatedPosition = -1;

    /**
     * Needed for getItemCount() returns 0 instead of real count of files
     * until we decide to show list animated.
     */
    private int mItemsCount = 0;

    /**
     * If true, mItemsCount will be 0 until showItems() have been called.
     */
    private boolean willEnterAnimation;

    /**
     * For notify client code that animation was finished.
     */
    private EnterAnimationListener mAnimationListener;

    public FileAdapter(Context context, boolean willEnterAnimation) {
        mContext = context;
        mIconsCache = new LruCache<>(ICONS_CACHE_SIZE);
        this.willEnterAnimation = willEnterAnimation;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        //start enter animation
        runEnterAnimation(holder.itemView, position);

        FileInfo fileInfo = mFilesInfo.get(position);
        holder.mFileNameView.setText(fileInfo.getName());
        //TODO format size using bytes, kilobytes etc.
        holder.mFileSizeView.setText(String.valueOf(fileInfo.getSize()));
        holder.mFileLastModifiedView.setText(mFormat.format(fileInfo.getLastModified()));

        Bitmap icon = getIconByType(fileInfo.getType());
        holder.mImageView.setImageBitmap(icon);
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    public void notifyRemoved(int position) {
        mItemsCount = mFilesInfo.size();
        notifyItemRemoved(position);
    }

    public void setWillEnterAnimation(boolean willEnterAnimation) {
        this.willEnterAnimation = willEnterAnimation;
    }

    private void runEnterAnimation(View view, int position) {
        if (!willEnterAnimation) {
            return;
        }

        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            view.setTranslationY(ScreenUtils.getScreenHeight(mContext));
            ViewPropertyAnimator animator = view.animate();

            animator
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(ITEM_ANIMATION_DURATION)
                    .start();
        }
    }

    private Bitmap getIconByType(final FileInfo.Type type) {
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

    public void setFilesInfo(ArrayList<FileInfo> filesInfo) {
        mFilesInfo = filesInfo;

        //if we will do enter animation, we won't show items immediately
        if(willEnterAnimation) {
            mItemsCount = 0;
        } else {
            mItemsCount = mFilesInfo.size();
            mLastAnimatedPosition = mItemsCount;
            notifyDataSetChanged();
        }
    }

    public FileInfo getFileInfo(int position) {
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

    /**
     * Sets to mItemsCount the real count of elements and call
     * notifyDataSetChange(). First items will we showed with enter animation.
     */
    public void showItems() {
        mItemsCount = mFilesInfo.size();
        notifyDataSetChanged();
    }

    public interface EnterAnimationListener {
        void onAnimationEnd();
    }
}

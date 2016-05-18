package net.vladykin.filemanager.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.FileSourceItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for display list of FileSourceItems.
 *
 * @author Vladimir Vladykin
 */
public final class FileSourcesAdapter extends RecyclerView.Adapter<FileSourcesAdapter.FileSourceHolder> {

    private List<FileSourceItem> items;
    @Nullable FileSourceClickListener clickListener;

    public interface FileSourceClickListener {
        void onFileSourceClick(FileSourceItem source);
    }

    public void setItems(List<FileSourceItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setClickListener(@Nullable FileSourceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public FileSourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileSourceHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_source, parent, false));
    }

    @Override
    public void onBindViewHolder(FileSourceHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public final class FileSourceHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.file_source_icon) ImageView icon;
        @Bind(R.id.file_source_title) TextView title;

        public FileSourceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                if (clickListener == null) {
                    return;
                }

                int adapterPosition = getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                clickListener.onFileSourceClick(items.get(adapterPosition));
            });
        }

        public void bind(FileSourceItem fileSource) {
            icon.setImageResource(fileSource.getIconId());
            title.setText(fileSource.getTitle());
        }
    }
}

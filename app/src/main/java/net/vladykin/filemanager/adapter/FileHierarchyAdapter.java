package net.vladykin.filemanager.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.entity.Node;
import net.vladykin.filemanager.model.source.FilesSource;
import net.vladykin.filemanager.util.callback.HierarchyNodeClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for show list of levels of files hierarchy.
 * It is designed to display items horizontally.
 *
 * @author Vladimir Vladykin
 */
public final class FileHierarchyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LEVEL = 0, DIVIDER = 1;

    private FilesSource source;
    private List<Node> nodes;
    private String rootDirectoryTitle;
    @Nullable private HierarchyNodeClickListener clickListener;

    public FileHierarchyAdapter(FilesSource source, String rootDirectoryTitle) {
        this.source = source;
        this.rootDirectoryTitle = rootDirectoryTitle;
        nodes = new ArrayList<>();
        prepareNodes();
    }

    public void notifyHierarchyUpdated() {
        prepareNodes();
        notifyDataSetChanged();
    }

    public void setClickListener(@Nullable HierarchyNodeClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case LEVEL:
                return new HierarchyLevelHolder(inflater.inflate(
                        R.layout.item_hierarchy_level, parent, false
                ));
            case DIVIDER:
                return new HierarchyDividerHolder(inflater.inflate(
                        R.layout.item_hierarchy_divider, parent, false
                ));
        }

        throw new IllegalStateException("Cannot parse viewType, should be LEVEL or DIVIDER.");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DIVIDER) {
            // nothing to do
            return;
        }

        HierarchyLevelHolder levelHolder = (HierarchyLevelHolder) holder;
        levelHolder.bind(nodeByAdapterPosition(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return calculateItemsCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1) {
            // we have only one level, so we can't have divider
            return LEVEL;
        }

        // dividers are always odd
        boolean isDivider = position % 2 != 0;
        return isDivider ? DIVIDER : LEVEL;
    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case LEVEL:
                return position;
            case DIVIDER:
                return Long.MAX_VALUE - position;
        }

        throw new IllegalStateException("Cannot parse item view type by position " + position);
    }

    /*
        We do a lot of allocations here, every time when we prepared nodes,
        but it should be ok for our case.
     */
    private void prepareNodes() {
        nodes.clear();

        File currentDirectory = source.getCurrentDirectory();
        if (currentDirectory == null) {
            // only root directory
            nodes.add(new Node(rootDirectoryTitle, null));
            return;
        }

        if (source.isRootDirectory(currentDirectory)) {
            // only root directory
            nodes.add(new Node(rootDirectoryTitle, currentDirectory));
            return;
        }


        nodes.add(0, new Node(currentDirectory.getName(), currentDirectory));

        File parentDirectory = currentDirectory.getParentFile();
        while (parentDirectory != null && !source.isRootDirectory(parentDirectory)) {
            nodes.add(0, new Node(parentDirectory.getName(), parentDirectory));

            // go up through hierarchy
            parentDirectory = parentDirectory.getParentFile();
        }

        nodes.add(0, new Node(rootDirectoryTitle, source.getRootDirectory()));
    }

    private int calculateItemsCount() {
        int levelsCount = nodes.size();
        if (levelsCount == 1) {
            // only root
            return 1;
        }

        int dividersCount = levelsCount - 1;
        return levelsCount + dividersCount;
    }

    private Node nodeByAdapterPosition(int adapterPosition) {
        if (adapterPosition == 0) {
            // this is root item
            return nodes.get(0);
        }

        final int dividersCountBeforePosition = adapterPosition / 2;
        final int positionWithoutDividers = adapterPosition - dividersCountBeforePosition;

        return nodes.get(positionWithoutDividers);
    }

    public final class HierarchyLevelHolder extends RecyclerView.ViewHolder {

        TextView levelTitleText;

        public HierarchyLevelHolder(View itemView) {
            super(itemView);
            levelTitleText = (TextView) itemView;

            levelTitleText.setOnClickListener(v -> {
                if (clickListener == null) {
                    return;
                }

                final int adapterPosition = getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                clickListener.onHierarchyNodeClick(nodeByAdapterPosition(adapterPosition));
            });
        }

        public void bind(CharSequence title) {
            levelTitleText.setText(title);
        }
    }

    public final class HierarchyDividerHolder extends RecyclerView.ViewHolder {

        public HierarchyDividerHolder(View itemView) {
            super(itemView);
        }
    }
}

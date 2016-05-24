package net.vladykin.filemanager.util.callback;

import net.vladykin.filemanager.entity.Node;

/**
 * Listens clicks on file system's hierarchy node.
 *
 * @see net.vladykin.filemanager.adapter.FileHierarchyAdapter
 * @author Vladimir Vladykin
 */
public interface HierarchyNodeClickListener {

    void onHierarchyNodeClick(Node node);
}

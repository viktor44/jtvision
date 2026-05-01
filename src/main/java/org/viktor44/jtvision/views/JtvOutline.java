package org.viktor44.jtvision.views;

import org.viktor44.jtvision.core.JtvRect;

/**
 * A concrete {@link JtvOutlineViewer} backed by a {@link JtvNode} linked tree.
 * <p>
 * JtvOutline wires the abstract tree-model methods of {@link JtvOutlineViewer}
 * to the {@link JtvNode} data structure. The tree root is stored in
 * {@link #root}; siblings are linked via {@link JtvNode#getNext()}, and
 * children are linked via {@link JtvNode#getChildList()}.
 * <p>
 * Expand/collapse state is maintained on each {@link JtvNode} directly through
 * {@link JtvNode#setExpanded(boolean)} and {@link JtvNode#isExpanded()}.
 * <p>
 * After construction, {@link JtvOutlineViewer#update()} is called automatically
 * to compute the initial scroll limit and focus position.
 *
 * @see JtvOutlineViewer
 * @see JtvNode
 */
public class JtvOutline extends JtvOutlineViewer {

    /**
     * The root node of the displayed tree. The remaining top-level nodes are
     * reachable via successive calls to {@link JtvNode#getNext()}.
     */
    private JtvNode root;

    /**
     * Constructs a TOutline with the given bounds, scroll bars, and root node.
     * Calls {@link JtvOutlineViewer#update()} to initialise the scroll limit.
     *
     * @param bounds        the bounding rectangle
     * @param aHScrollBar   the horizontal scroll bar, or {@code null}
     * @param aVScrollBar   the vertical scroll bar, or {@code null}
     * @param aRoot         the root of the node tree, or {@code null} for empty
     */
    public JtvOutline(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvNode aRoot) {
        super(bounds, aHScrollBar, aVScrollBar);
        root = aRoot;
        update();
    }

    /**
     * Expands or collapses {@code node} by calling
     * {@link JtvNode#setExpanded(boolean)}.
     *
     * @param node   the node to adjust; {@code null} is a no-op
     * @param expand {@code true} to expand, {@code false} to collapse
     */
    @Override
    public void adjust(JtvNode node, boolean expand) {
        if (node != null) {
            node.setExpanded(expand);
        }
    }

    /**
     * Returns the root node of this outline's tree.
     *
     * @return the root {@link JtvNode}, or {@code null} if the tree is empty
     */
    @Override
    public JtvNode getRoot() {
        return root;
    }

    /**
     * Returns the number of direct children of {@code node} by walking
     * {@code node.childList} via {@link JtvNode#getNext()}.
     *
     * @param node the node to query; {@code null} returns {@code 0}
     * @return the child count
     */
    @Override
    public int getNumChildren(JtvNode node) {
        int i = 0;
        JtvNode p = node != null ? node.getChildList() : null;
        while (p != null) {
            i++;
            p = p.getNext();
        }
        return i;
    }

    /**
     * Returns the next sibling of {@code node} via {@link JtvNode#getNext()}.
     *
     * @param node the reference node; {@code null} returns {@code null}
     * @return the next sibling, or {@code null}
     */
    @Override
    public JtvNode getNext(JtvNode node) {
        return node != null ? node.getNext() : null;
    }

    /**
     * Returns the {@code i}-th child of {@code node} by walking
     * {@code node.childList} {@code i} steps via {@link JtvNode#getNext()}.
     *
     * @param node the parent node; {@code null} returns {@code null}
     * @param i    the zero-based child index
     * @return the child at index {@code i}, or {@code null} if out of range
     */
    @Override
    public JtvNode getChild(JtvNode node, int i) {
        JtvNode p = node != null ? node.getChildList() : null;
        while (i > 0 && p != null) {
            i--;
            p = p.getNext();
        }
        return p;
    }

    /**
     * Returns the display text of {@code node} via {@link JtvNode#getText()}.
     *
     * @param node the node to describe; {@code null} returns an empty string
     * @return the node's text label, or {@code ""}
     */
    @Override
    public String getText(JtvNode node) {
        return node != null ? node.getText() : "";
    }

    /**
     * Returns {@code true} if {@code node} is expanded.
     *
     * @param node the node to test; {@code null} returns {@code false}
     * @return {@code true} if the node is expanded
     */
    @Override
    public boolean isExpanded(JtvNode node) {
        return node != null && node.isExpanded();
    }

    /**
     * Returns {@code true} if {@code node} has at least one child
     * (i.e. {@code node.childList} is non-{@code null}).
     *
     * @param node the node to test; {@code null} returns {@code false}
     * @return {@code true} if the node has children
     */
    @Override
    public boolean hasChildren(JtvNode node) {
        return node != null && node.getChildList() != null;
    }
}

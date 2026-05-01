package org.viktor44.jtvision.views;

import lombok.Getter;
import lombok.Setter;

/**
 * A general-purpose tree node used by {@link JtvOutline} and
 * {@link JtvOutlineViewer}.
 * <p>
 * TNode stores a display label and links to a sibling ({@link #next}) and a
 * child list ({@link #childList}). Children are themselves TNode instances,
 * linked through their own {@code next} pointers. The {@link #expanded} flag
 * controls whether children are visible in the outline viewer.
 * 
 * <h3>Tree structure example</h3>
 * 
 * <pre>
 * root ──next──► sibling
 *  │
 * childList
 *  │
 *  child1 ──next──► child2
 * </pre>
 * Traversal: start at the root, follow {@code childList} for depth-first
 * descent, follow {@code next} for sibling iteration.
 */
public class JtvNode {

    /**
     * The next sibling of this node at the same tree level, or {@code null} if this is the last sibling.
     */
	@Getter
	@Setter
    private JtvNode next;

	/**
     * The text label displayed in the outline viewer for this node.
     */
	@Getter
	@Setter
    private String text;

    /**
     * The first child of this node, or {@code null} if the node is a leaf.
     * Remaining children are reached by following {@link #next} on this node.
     */
	@Getter
	@Setter
    private JtvNode childList;

    /**
     * Whether this node's children are currently expanded (visible) in the outline. {@code true} by default.
     */
	@Getter
	@Setter
    private boolean expanded;

    /**
     * Constructs a leaf node with the given label, no children, no sibling,
     * and {@link #expanded} set to {@code true}.
     *
     * @param aText the display label for this node
     */
    public JtvNode(String aText) {
        this(aText, null, null, true);
    }

    /**
     * Constructs a fully specified node.
     *
     * @param aText         the display label
     * @param aChildren     the first child node, or {@code null} for a leaf
     * @param aNext         the next sibling node, or {@code null}
     * @param initialState  {@code true} if the node starts expanded
     */
    public JtvNode(String aText, JtvNode aChildren, JtvNode aNext, boolean initialState) {
        next = aNext;
        text = aText;
        childList = aChildren;
        expanded = initialState;
    }
}

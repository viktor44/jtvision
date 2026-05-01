/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmOutlineItemSelected;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseAuto;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;

import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;

/**
 * <p>
 * Abstract base class for tree-outline views.
 * <p>
 * JtvOutlineViewer extends {@link JtvScroller} to display a hierarchical tree
 * structure, with each node rendered as a graph prefix (branch lines and
 * expand/collapse glyphs) followed by the node's text label.
 * 
 * <h3>Tree model</h3>
 * 
 * The concrete subclass supplies the tree by implementing the abstract
 * accessor methods ({@link #getRoot()}, {@link #getNext(JtvNode)},
 * {@link #getChild(JtvNode, int)}, {@link #getNumChildren(JtvNode)},
 * {@link #getText(JtvNode)}, {@link #hasChildren(JtvNode)},
 * {@link #isExpanded(JtvNode)}). The viewer itself does not own the data.
 * Mutations (expand/collapse) are delegated back to the subclass via
 * {@link #adjust(JtvNode, boolean)}.
 * 
 * <h3>Rendering</h3>
 * 
 * {@link #update()} traverses the visible (expanded) nodes into a flat list,
 * computes the maximum line width, and updates the scroll limit. {@link #draw()}
 * renders the visible window using that flat list. Graph prefixes are produced
 * by {@link #getGraph(int, long, int)}, which delegates to the lower-level
 * {@link #createGraph(int, long, int, int, int, char[])} for the
 * actual ASCII-art tree connector rendering.
 * 
 * <h3>Node flags</h3>
 * 
 * <ul>
 *   <li>{@link #ovExpanded} — the node is expanded (its children are visible).</li>
 *   <li>{@link #ovChildren} — the node has children.</li>
 *   <li>{@link #ovLast} — the node is the last sibling at its level.</li>
 * </ul>
 * 
 * <h3>Keyboard and mouse interaction</h3>
 * 
 * Arrow keys, Page Up/Down, Home, End, Ctrl+Page Up/Down navigate the focus.
 * Enter double-click broadcasts {@code cmOutlineItemSelected}. The {@code +},
 * {@code -}, and {@code *} keys expand, collapse, or recursively expand the
 * focused node. Clicking within the graph prefix area toggles expansion;
 * clicking on the text area moves the focus.
 * 
 * <h3>Colour palette</h3>
 * 
 * The four-entry palette {@link #cpOutlineViewer} maps to:
 * <ol>
 *   <li>{@code 0x06} — normal text.</li>
 *   <li>{@code 0x07} — focused item.</li>
 *   <li>{@code 0x03} — selected item.</li>
 *   <li>{@code 0x08} — graph/connector colour.</li>
 * </ol>
 *
 * @see JtvOutline
 * @see JtvNode
 */
public abstract class JtvOutlineViewer extends JtvScroller {

    /**
     * Node flag: the node is currently expanded and its children are shown.
     */
    protected static final int ovExpanded = 0x01;

    /**
     * Node flag: the node has at least one child.
     */
    protected static final int ovChildren = 0x02;

    /**
     * Node flag: the node is the last sibling in its parent's child list.
     * When set, the connector uses a corner glyph ({@code \}) rather than
     * a T-junction ({@code +}).
     */
    protected static final int ovLast = 0x04;

    /**
     * The index in the flat visible-node list of the currently focused node.
     */
    protected int foc;

    /**
     * Four-entry colour palette:
     * <ol>
     *   <li>{@code 0x06} — normal text.</li>
     *   <li>{@code 0x07} — focused item.</li>
     *   <li>{@code 0x03} — selected item.</li>
     *   <li>{@code 0x08} — graph/connector colour.</li>
     * </ol>
     */
    private static final JtvPalette cpOutlineViewer = new JtvPalette(new int[] {0x06, 0x07, 0x03, 0x08});

    /**
     * Constructs an outline viewer with the given bounds and optional scroll
     * bars. Sets {@code ofSelectable} so the viewer can receive focus.
     *
     * @param bounds        the bounding rectangle
     * @param aHScrollBar   the horizontal scroll bar, or {@code null}
     * @param aVScrollBar   the vertical scroll bar, or {@code null}
     */
    public JtvOutlineViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar) {
        super(bounds, aHScrollBar, aVScrollBar);
        options |= ofSelectable;
        foc = 0;
    }

    /**
     * Expands or collapses {@code node} according to {@code expand}.
     * Subclasses implement this to mutate the underlying data model.
     *
     * @param node   the node to adjust
     * @param expand {@code true} to expand, {@code false} to collapse
     */
    public abstract void adjust(JtvNode node, boolean expand);

    /**
     * Returns the next sibling of {@code node} at the same tree level,
     * or {@code null} if there is no next sibling.
     *
     * @param node the reference node
     * @return the next sibling, or {@code null}
     */
    public abstract JtvNode getNext(JtvNode node);

    /**
     * Returns the {@code i}-th child of {@code node} (0-based index).
     *
     * @param node the parent node
     * @param i    the zero-based child index
     * @return the child node at index {@code i}, or {@code null}
     */
    public abstract JtvNode getChild(JtvNode node, int i);

    /**
     * Returns the number of direct children of {@code node}.
     *
     * @param node the node to query
     * @return the child count
     */
    public abstract int getNumChildren(JtvNode node);

    /**
     * Returns the root node of the tree. The outline traversal starts from
     * this node.
     *
     * @return the root node, or {@code null} for an empty tree
     */
    public abstract JtvNode getRoot();

    /**
     * Returns the display text for {@code node}.
     *
     * @param node the node to describe
     * @return the node's text label
     */
    public abstract String getText(JtvNode node);

    /**
     * Returns {@code true} if {@code node} has at least one child.
     *
     * @param node the node to test
     * @return {@code true} if the node has children
     */
    public abstract boolean hasChildren(JtvNode node);

    /**
     * Returns {@code true} if {@code node}'s children are currently visible
     * (expanded).
     *
     * @param node the node to test
     * @return {@code true} if the node is expanded
     */
    public abstract boolean isExpanded(JtvNode node);

    /**
     * Returns the outline viewer's colour palette {@link #cpOutlineViewer}.
     *
     * @return the four-entry palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpOutlineViewer;
    }

    /**
     * Generates the ASCII-art graph prefix string for a node at the given
     * depth using default connector characters.
     * <p>
     * The character set is {@code {' ', '|', '+', '\\', '-', '-', '+', '-'}}
     * with a level width of 3 and an end section width of 3.
     *
     * @param level the zero-based depth of the node in the tree
     * @param lines a bitmask where bit {@code l} is set if level {@code l}
     *              has a continuing vertical line (i.e. the ancestor at that
     *              level is not the last sibling)
     * @param flags a combination of {@link #ovExpanded}, {@link #ovChildren},
     *              and {@link #ovLast}
     * @return the graph prefix string
     */
    public String getGraph(int level, long lines, int flags) {
        final int levelWidth = 3;
        final int endWidth = levelWidth;
        final char[] graphChars = new char[] {' ', '|', '+', '\\', '-', '-', '+', '-'};
        return createGraph(level, lines, flags, levelWidth, endWidth, graphChars);
    }

    /**
     * Builds the graph prefix string using the supplied character set and
     * width parameters.
     * <p>
     * The prefix has two parts:
     * <ol>
     *   <li><b>Level columns</b> ({@code level × levWidth} characters): for
     *       each ancestor level, a {@code chars[1]} ({@code |}) vertical bar
     *       if that level has more siblings, or {@code chars[0]} (space)
     *       otherwise, followed by spaces to fill {@code levWidth}.</li>
     *   <li><b>End section</b> ({@code endWidth} characters): a corner/T
     *       glyph, optional dashes, a child indicator, and an expand/collapse
     *       glyph.</li>
     * </ol>
     *
     * @param level    the zero-based depth of the node
     * @param lines    bitmask of levels with continuing vertical lines
     * @param flags    {@link #ovExpanded}, {@link #ovChildren}, {@link #ovLast}
     * @param levWidth number of characters per ancestor level column
     * @param endWidth number of characters in the end (connector) section
     * @param chars    eight-character glyph array:
     *                 {@code [space, vbar, tJunction, corner, dash, dash, closedBox, dash]}
     * @return the assembled graph prefix string
     */
    protected String createGraph(int level, long lines, int flags, int levWidth, int endWidth, char[] chars) {
        StringBuilder sb = new StringBuilder(level * levWidth + endWidth + 1);
        boolean expanded = (flags & ovExpanded) != 0;
        boolean children = (flags & ovChildren) != 0;
        boolean last = (flags & ovLast) != 0;

        for (int l = level; l > 0; l--, lines >>= 1) {
            sb.append((lines & 1L) != 0 ? chars[1] : chars[0]);
            for (int i = 1; i < levWidth; i++) {
                sb.append(chars[0]);
            }
        }

        if (--endWidth > 0) {
            sb.append(last ? chars[3] : chars[2]);
            if (--endWidth > 0) {
                if (--endWidth > 0) {
                    for (int i = 0; i < endWidth; i++) {
                        sb.append(chars[4]);
                    }
                }
                sb.append(children ? chars[5] : chars[4]);
            }
            sb.append(expanded ? chars[7] : chars[6]);
        }
        return sb.toString();
    }

    /**
     * Sets the focused index to {@code i} without scrolling or redrawing.
     * Called internally during focus adjustments.
     *
     * @param i the new focused index in the flat visible-node list
     */
    public void focused(int i) {
        foc = i;
    }

    /**
     * Returns {@code true} if visible-node index {@code i} equals the current
     * focus index {@link #foc}.
     *
     * @param i the index to test
     * @return {@code true} if the node at {@code i} is focused
     */
    public boolean isSelected(int i) {
        return foc == i;
    }

    /**
     * Broadcasts {@code cmOutlineItemSelected} with the node at position
     * {@code i} as the info pointer, notifying the owner that the user has
     * activated an item.
     *
     * @param i the visible-node index of the selected node
     */
    public void selected(int i) {
        JtvNode node = getNode(i);
        if (node != null) {
            message(owner, org.viktor44.jtvision.core.EventCodes.evBroadcast, cmOutlineItemSelected, node);
        }
    }

    /**
     * Rebuilds the flat visible-node list, recalculates the maximum line
     * width, updates the scroll limit via {@link #setLimit(int, int)}, and
     * clamps the focus into the new range.
     * <p>
     * Must be called after any structural change to the tree (expand, collapse,
     * insert, or delete).
     */
    public void update() {
        List<VisibleNode> nodes = visibleNodes();
        int maxX = 0;
        for (VisibleNode vn : nodes) {
            int len = getGraph(vn.level, vn.lines, vn.flags).length();
            String text = getText(vn.node);
            if (text != null) {
                len += text.length();
            }
            if (len > maxX) {
                maxX = len;
            }
        }
        setLimit(maxX, nodes.size());
        adjustFocus(foc);
    }

    /**
     * Renders the visible rows of the outline.
     * <p>
     * For each visible row, computes the corresponding visible-node index as
     * {@code row + delta.y}. The graph prefix and text are drawn with a colour
     * determined by whether the node is focused ({@code sfFocused}), selected,
     * or normal. The horizontal scroll offset ({@code delta.x}) is applied
     * to both the graph and text portions.
     */
    @Override
    public void draw() {
        JtvColorAttr normal = getColor(1);
        JtvDrawBuffer dBuf = new JtvDrawBuffer();
        List<VisibleNode> nodes = visibleNodes();

        for (int y = 0; y < size.getY(); y++) {
            dBuf.moveChar(0, ' ', normal, size.getX());
            int pos = y + delta.getY();
            if (pos >= 0 && pos < nodes.size()) {
                VisibleNode vn = nodes.get(pos);
                JtvColorAttr color = normal;
                if (pos == foc && (state & sfFocused) != 0) {
                    color = getColor(2);
                }
                else if (isSelected(pos)) {
                    color = getColor(3);
                }
                dBuf.moveChar(0, ' ', color, size.getX());

                String graph = getGraph(vn.level, vn.lines, vn.flags);
                int x = graph.length() - Math.max(0, delta.getX());
                if (x > 0) {
                    dBuf.moveStr(0, graph, color, size.getX(), Math.max(0, delta.getX()));
                }

                String text = getText(vn.node);
                if (text != null) {
                    int tx = Math.max(0, x);
                    int indent = Math.max(0, -x);
                    dBuf.moveStr(tx, text, color, size.getX() - tx, indent);
                }
            }
            writeLine(0, y, size.getX(), 1, dBuf);
        }
    }

    /**
     * Returns the {@link JtvNode} at the given visible-node {@code position},
     * or {@code null} if the position is out of range.
     *
     * @param position the zero-based index in the flat visible-node list
     * @return the node, or {@code null}
     */
    protected JtvNode getNode(int position) {
        List<VisibleNode> nodes = visibleNodes();
        if (position >= 0 && position < nodes.size()) {
            return nodes.get(position).node;
        }
        return null;
    }

    /**
     * Clamps {@code newFocus} to {@code [0, limit.y - 1]}, updates
     * {@link #foc} via {@link #focused(int)}, and scrolls the viewport
     * vertically to ensure the focused row is visible.
     *
     * @param newFocus the desired focus index
     */
    protected void adjustFocus(int newFocus) {
        if (newFocus < 0) {
            newFocus = 0;
        }
        else if (newFocus >= limit.getY()) {
            newFocus = Math.max(0, limit.getY() - 1);
        }
        if (foc != newFocus) {
            focused(newFocus);
        }
        if (newFocus < delta.getY()) {
            scrollTo(delta.getX(), newFocus);
        }
        else if (newFocus - size.getY() >= delta.getY()) {
            scrollTo(delta.getX(), newFocus - size.getY() + 1);
        }
    }

    /**
     * Recursively expands {@code node} and all of its descendants.
     * After calling this method, {@link #update()} and {@link #drawView()}
     * should be called to refresh the display.
     *
     * @param node the root of the subtree to expand; {@code null} is a no-op
     */
    public void expandAll(JtvNode node) {
        if (node == null) {
            return;
        }
        if (hasChildren(node)) {
            adjust(node, true);
            int n = getNumChildren(node);
            for (int i = 0; i < n; i++) {
                expandAll(getChild(node, i));
            }
        }
    }

    /**
     * Propagates the state change and redraws when {@code sfFocused} changes,
     * so the focused-item highlight colour is updated immediately.
     *
     * @param aState the state bits to modify
     * @param enable {@code true} to set; {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & sfFocused) != 0) {
            drawView();
        }
    }

    /**
     * Handles mouse clicks and keyboard navigation.
     * <p>
     * <b>Mouse ({@code evMouseDown}):</b> tracks the pointer with auto-scroll,
     * updating focus via {@link #adjustFocus(int)}. A double-click fires
     * {@link #selected(int)}. A single click within the graph prefix area
     * toggles the node's expand/collapse state via {@link #adjust(JtvNode, boolean)}.
     * <p>
     * <b>Keyboard ({@code evKeyDown}):</b>
     * <ul>
     *   <li>Up/Left — move focus up one row.</li>
     *   <li>Down/Right — move focus down one row.</li>
     *   <li>Page Up/Down — move focus by {@code size.y - 1}.</li>
     *   <li>Home/End — move to first/last visible row.</li>
     *   <li>Ctrl+Page Up/Down — move to absolute first/last node.</li>
     *   <li>Enter — fire {@link #selected(int)}.</li>
     *   <li>{@code +}/{@code -} — expand or collapse focused node.</li>
     *   <li>{@code *} — recursively expand focused subtree.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        int newFocus = foc;
        if (event.getWhat() == evMouseDown) {
            int count = 0;
            int dragged = 0;
            do {
                if (dragged < 2) {
                    dragged++;
                }
                JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                if (mouseInView(event.getMouse().getWhere())) {
                    int i = delta.getY() + mouse.getY();
                    newFocus = (i < limit.getY()) ? i : foc;
                }
                else {
                    if (event.getWhat() == evMouseAuto) {
                        count++;
                    }
                    if (count == 3) {
                        count = 0;
                        if (mouse.getY() < 0) {
                            newFocus--;
                        }
                        if (mouse.getY() >= size.getY()) {
                            newFocus++;
                        }
                    }
                }
                if (foc != newFocus) {
                    adjustFocus(newFocus);
                    drawView();
                }
            }
            while (mouseEvent(event, evMouseMove | evMouseAuto));

            if ((event.getMouse().getEventFlags() & meDoubleClick) != 0) {
                selected(foc);
            }
            else if (dragged < 2) {
                JtvNode cur = getNode(newFocus);
                if (cur != null) {
                    String graph = getGraphForPosition(newFocus);
                    JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                    mouse = new JtvPoint(mouse.getX() + delta.getX(), mouse.getY());
                    if (mouse.getX() < graph.length()) {
                        adjust(cur, !isExpanded(cur));
                        update();
                        drawView();
                    }
                }
            }
            clearEvent(event);
            return;
        }

        if (event.getWhat() == evKeyDown) {
            switch (JtvKey.ctrlToArrow(event.getKeyDown().getKeyStroke())) {
                case JtvKey.kbUp:
                case JtvKey.kbLeft:
                    newFocus--;
                    break;
                case JtvKey.kbDown:
                case JtvKey.kbRight:
                    newFocus++;
                    break;
                case JtvKey.kbPgDn:
                    newFocus += size.getY() - 1;
                    break;
                case JtvKey.kbPgUp:
                    newFocus -= size.getY() - 1;
                    break;
                case JtvKey.kbHome:
                    newFocus = delta.getY();
                    break;
                case JtvKey.kbEnd:
                    newFocus = delta.getY() + size.getY() - 1;
                    break;
                case JtvKey.kbCtrlPgUp:
                    newFocus = 0;
                    break;
                case JtvKey.kbCtrlPgDn:
                    newFocus = limit.getY() - 1;
                    break;
                case JtvKey.kbEnter:
                    selected(newFocus);
                    break;
                default:
                    char ch = event.getKeyDown().getKeyChar();
                    if (ch == '-' || ch == '+') {
                        JtvNode cur = getNode(newFocus);
                        if (cur != null) {
                            adjust(cur, ch == '+');
                            update();
                        }
                    }
                    else if (ch == '*') {
                        JtvNode cur = getNode(newFocus);
                        if (cur != null) {
                            expandAll(cur);
                            update();
                        }
                    }
                    else {
                        return;
                    }
            }
            clearEvent(event);
            adjustFocus(newFocus);
            drawView();
        }
    }

    /**
     * Returns the graph-prefix string for the visible node at {@code pos}.
     * Used during mouse handling to determine whether a click landed in the
     * graph area (triggering expand/collapse) or the text area.
     *
     * @param pos the visible-node index
     * @return the graph prefix string, or an empty string if out of range
     */
    private String getGraphForPosition(int pos) {
        List<VisibleNode> nodes = visibleNodes();
        if (pos < 0 || pos >= nodes.size()) {
            return "";
        }
        VisibleNode vn = nodes.get(pos);
        return getGraph(vn.level, vn.lines, vn.flags);
    }

    /**
     * Builds the flat list of currently visible (i.e. reachable through
     * expanded ancestors) nodes by performing a depth-first traversal
     * starting from {@link #getRoot()}.
     *
     * @return ordered list of visible nodes from top to bottom
     */
    private List<VisibleNode> visibleNodes() {
        List<VisibleNode> out = new ArrayList<VisibleNode>();
        JtvNode root = getRoot();
        if (root == null) {
            return out;
        }
        traverseInto(root, 0, 0L, getNext(root) == null, out);
        return out;
    }

    /**
     * Recursively adds {@code cur} and, if expanded, its subtree to
     * {@code out}. After processing the root's subtree, iterates over
     * the root's siblings (returned by {@link #getNext(JtvNode)}) at the
     * same level.
     *
     * @param cur       the node to visit
     * @param level     the zero-based depth of {@code cur}
     * @param lines     bitmask of ancestor levels with continuing vertical lines
     * @param lastChild {@code true} if {@code cur} is the last sibling
     * @param out       the accumulator list
     */
    private void traverseInto(JtvNode cur, int level, long lines, boolean lastChild, List<VisibleNode> out) {
        if (cur == null) {
            return;
        }
        boolean children = hasChildren(cur);
        int flags = 0;
        if (lastChild) {
            flags |= ovLast;
        }
        if (children && isExpanded(cur)) {
            flags |= ovChildren;
        }
        if (!children || isExpanded(cur)) {
            flags |= ovExpanded;
        }
        out.add(new VisibleNode(cur, level, lines, flags));

        if (children && isExpanded(cur)) {
            long childLines = lines;
            if (!lastChild) {
                childLines |= (1L << level);
            }
            int n = getNumChildren(cur);
            for (int i = 0; i < n; i++) {
                JtvNode child = getChild(cur, i);
                traverseInto(child, level + 1, childLines, i == n - 1, out);
            }
        }
        if (cur == getRoot()) {
            JtvNode next = cur;
            while ((next = getNext(next)) != null) {
                traverseInto(next, level, lines, getNext(next) == null, out);
            }
        }
    }

    /**
     * Immutable snapshot of a node's rendering state in the flat visible list.
     * Captures everything needed to draw one row without re-traversing the tree.
     */
    protected static final class VisibleNode {

        /** The tree node this entry represents. */
        private final JtvNode node;

        /** Zero-based depth of the node in the tree. */
        private final int level;

        /**
         * Bitmask where bit {@code l} is set if the ancestor at level {@code l}
         * has more siblings below it, requiring a vertical line through this row.
         */
        private final long lines;

        /**
         * Combination of {@link #ovExpanded}, {@link #ovChildren}, and
         * {@link #ovLast} describing this node's rendering state.
         */
        private final int flags;

        /**
         * Constructs a visible-node snapshot.
         *
         * @param node  the tree node
         * @param level the node's depth
         * @param lines the ancestor-line bitmask
         * @param flags the rendering flags
         */
        VisibleNode(JtvNode node, int level, long lines, int flags) {
            this.node = node;
            this.level = level;
            this.lines = lines;
            this.flags = flags;
        }
    }
}

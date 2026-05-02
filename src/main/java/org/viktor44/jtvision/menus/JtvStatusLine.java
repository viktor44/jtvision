/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import static org.viktor44.jtvision.core.CommandCodes.cmCommandSetChanged;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoY;
import static org.viktor44.jtvision.core.ViewFlags.hcNoContext;
import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.StringUtils;
import org.viktor44.jtvision.views.JtvView;

/**
 * The application status line, typically displayed at the bottom of the screen.
 * <p>
 * JtvStatusLine renders a single row of {@link JtvStatusItem} labels and an
 * optional context-sensitive hint string. It is normally owned by the
 * application group and is one of the three standard application sub-views
 * (together with the menu bar and desktop).
 * 
 * <h3>Context-sensitive display</h3>
 * 
 * The status line holds an ordered list of {@link JtvStatusDef} records, each
 * covering a range of help context values. {@link #update()} is called from
 * the application idle loop; it queries the help context of the currently
 * focused view and selects the first {@link JtvStatusDef} whose range contains
 * that context. The matching definition's {@link JtvStatusItem} list becomes
 * the active {@link #items} list and is rendered by {@link #draw()}.
 * 
 * <h3>Hints</h3>
 * 
 * After drawing the status items, {@link #draw()} calls {@link #hint(int)}
 * with the current help context. If the returned string is non-empty it is
 * drawn to the right of a vertical separator ({@code │ }). Subclasses can
 * override {@link #hint(int)} to supply dynamic hint text.
 * 
 * <h3>Hot keys</h3>
 * 
 * {@link JtvStatusItem#keyCode} values are scanned during {@code evKeyDown}
 * events: a match converts the key event to an {@code evCommand} event with
 * the item's command, providing application-wide shortcut key bindings.
 * Clicking on a label area triggers the same command via {@code evMouseDown}.
 * 
 * <h3>Grow mode</h3>
 * 
 * The status line's {@code growMode} is set to
 * {@code gfGrowLoY | gfGrowHiX | gfGrowHiY} so it stays pinned to the
 * bottom edge and stretches horizontally when the application is resized.
 * 
 * <h3>Colour palette</h3>
 * 
 * The six-entry palette {@link #cpStatusLine} maps to:
 * <ol>
 *   <li>{@code 2} — normal text.</li>
 *   <li>{@code 3} — disabled text.</li>
 *   <li>{@code 4} — shortcut letter.</li>
 *   <li>{@code 5} — selected shortcut.</li>
 *   <li>{@code 6} — selected normal.</li>
 *   <li>{@code 7} — selected disabled.</li>
 * </ol>
 *
 * @see JtvStatusDef
 * @see JtvStatusItem
 */
public class JtvStatusLine extends JtvView {

    /**
     * Six-entry colour palette:
     * <ol>
     *   <li>{@code 2} — normal text.</li>
     *   <li>{@code 3} — disabled text.</li>
     *   <li>{@code 4} — shortcut letter.</li>
     *   <li>{@code 5} — selected shortcut.</li>
     *   <li>{@code 6} — selected normal.</li>
     *   <li>{@code 7} — selected disabled.</li>
     * </ol>
     */
    private static final JtvPalette cpStatusLine = new JtvPalette(new int[] {2, 3, 4, 5, 6, 7});

    /**
     * The separator drawn between the status items and the hint text:
     * {@code "│ "} (vertical bar followed by a space).
     */
    private static final String hintSeparator = "│ ";

    /**
     * The ordered list of {@link JtvStatusDef} records. {@link #update()}
     * walks this list to find the definition appropriate for the current
     * help context.
     */
    private List<JtvStatusDef> defs = new ArrayList<>();

    /**
     * The currently active {@link JtvStatusItem} list, selected from
     * {@link #defs} by {@link #findItems()}. Rendered by {@link #draw()}.
     */
    private List<JtvStatusItem> items = Collections.emptyList();

    /**
     * Constructs a status line with the given bounds and definition list.
     * Sets {@code ofPreProcess} in options (for hot-key pre-processing),
     * adds {@code evBroadcast} to the event mask, sets grow mode to
     * {@code gfGrowLoY | gfGrowHiX | gfGrowHiY}, and calls
     * {@link #findItems()} to select the initial item list.
     *
     * @param bounds the bounding rectangle (typically the bottom row)
     * @param aDefs  the initial status definition, or {@code null}
     */
    public JtvStatusLine(JtvRect bounds, JtvStatusDef aDefs) {
        super(bounds);
        if (aDefs != null) {
            defs.add(aDefs);
        }
        options |= ofPreProcess;
        eventMask |= evBroadcast;
        growMode = gfGrowLoY | gfGrowHiX | gfGrowHiY;
        findItems();
    }

    public JtvStatusLine addDefinition(JtvStatusDef item) {
        if (item != null) {
            defs.add(item);
        }
        return this;
    }

    /**
     * Draws the status line by delegating to {@link #drawSelect(JtvStatusItem)}
     * with no item highlighted.
     */
    @Override
    public void draw() {
        drawSelect(null);
    }

    /**
     * Renders the full status line row, optionally highlighting one item.
     * <p>
     * Each item in {@link #items} whose {@link JtvStatusItem#text} is
     * non-{@code null} is drawn with surrounding spaces. The colour is:
     * <ul>
     *   <li>Selected and enabled — {@code cSelect}.</li>
     *   <li>Selected and disabled — {@code cSelDisabled}.</li>
     *   <li>Enabled — {@code cNormal}.</li>
     *   <li>Disabled — {@code cNormDisabled}.</li>
     * </ul>
     * After all items, if space remains and {@link #hint(int)} returns a
     * non-empty string, the hint is drawn following {@link #hintSeparator}.
     *
     * @param selected the item to highlight as pressed, or {@code null}
     *                 for normal (non-pressed) drawing
     */
    public void drawSelect(JtvStatusItem selected) {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr cNormal = getColor(1), cNormalHot = getColor(3);
        JtvColorAttr cSelect = getColor(4), cSelectHot = getColor(6);
        JtvColorAttr cNormDisabled = getColor(2), cNormDisabledHot = getColor(2);
        JtvColorAttr cSelDisabled = getColor(5), cSelDisabledHot = getColor(5);

        b.moveChar(0, ' ', cNormal, size.getX());
        int i = 0;

        for (JtvStatusItem t : items) {
            if (t.getText() != null) {
                int l = StringUtils.cstrLen(t.getText());
                if (i + l < size.getX()) {
                    JtvColorAttr color, colorHot;
                    if (commandEnabled(t.getCommand())) {
                        if (t == selected) { color = cSelect; colorHot = cSelectHot; }
                        else { color = cNormal; colorHot = cNormalHot; }
                    } else {
                        if (t == selected) { color = cSelDisabled; colorHot = cSelDisabledHot; }
                        else { color = cNormDisabled; colorHot = cNormDisabledHot; }
                    }
                    b.moveChar(i, ' ', color, 1);
                    b.moveCStr(i + 1, t.getText(), color, colorHot);
                    b.moveChar(i + l + 1, ' ', color, 1);
                }
                i += l + 2;
            }
        }

        if (i < size.getX() - 2) {
            String hintText = hint(helpCtx);
            if (hintText != null && !hintText.isEmpty()) {
                b.moveStr(i, hintSeparator, cNormal);
                i += 2;
                b.moveStr(i, hintText, cNormal, size.getX() - i, 0);
            }
        }

        writeLine(0, 0, size.getX(), 1, b);
    }

    /**
     * Selects the active item list by finding the first {@link JtvStatusDef}
     * in {@link #defs} whose range contains {@link JtvView#helpCtx}. Sets
     * {@link #items} to that definition's item list, or {@code null} if no
     * definition matches.
     */
    public void findItems() {
        JtvStatusDef match = null;
        for (JtvStatusDef p : defs) {
            if (helpCtx >= p.getMin() && helpCtx <= p.getMax()) {
                match = p;
                break;
            }
        }
        items = (match != null) ? match.getItems() : Collections.emptyList();
    }

    /**
     * Returns the status line's colour palette {@link #cpStatusLine}.
     *
     * @return the six-entry palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpStatusLine;
    }

    /**
     * Returns the {@link JtvStatusItem} whose label area contains the given
     * view-local mouse position, or {@code null} if the pointer is not over
     * any item.
     * <p>
     * Only items in row 0 ({@code mouse.y == 0}) are considered. Items are
     * laid out sequentially from column 0; each item occupies
     * {@code cstrLen(text) + 2} columns.
     *
     * @param mouse the pointer position in view-local coordinates
     * @return the item under the pointer, or {@code null}
     */
    public JtvStatusItem itemMouseIsIn(JtvPoint mouse) {
        if (mouse.getY() != 0) return null;
        int i = 0;
        for (JtvStatusItem t : items) {
            if (t.getText() != null) {
                int k = i + StringUtils.cstrLen(t.getText()) + 2;
                if (mouse.getX() >= i && mouse.getX() < k)
                    return t;
                i = k;
            }
        }
        return null;
    }

    /**
     * Handles events for the status line.
     * <p>
     * <b>{@code evMouseDown}:</b> tracks the pointer while the button is
     * held, calling {@link #drawSelect(JtvStatusItem)} to highlight the item
     * under the pointer. On button release, if the pointer is still over
     * an enabled item, posts an {@code evCommand} event with that item's
     * command and clears the mouse event.
     * <p>
     * <b>{@code evKeyDown}:</b> scans {@link #items} for a matching
     * {@link JtvStatusItem#keyCode}. On match, converts the event to an
     * {@code evCommand} with that item's command (leaving it for the
     * application to process).
     * <p>
     * <b>{@code evBroadcast cmCommandSetChanged}:</b> redraws the status
     * line to reflect any hot key enable/disable changes.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        switch (event.getWhat()) {
            case evMouseDown: {
                JtvStatusItem t = null;
                do {
                    JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                    JtvStatusItem newT = itemMouseIsIn(mouse);
                    if (t != newT) {
                        t = newT;
                        drawSelect(t);
                    }
                } while (mouseEvent(event, evMouseMove));

                if (t != null && commandEnabled(t.getCommand())) {
                    event.setWhat(evCommand);
                    event.getMessage().setCommand(t.getCommand());
                    event.getMessage().setInfoPtr(null);
                    putEvent(event);
                }
                clearEvent(event);
                drawView();
                break;
            }
            case evKeyDown:
                if (event.getKeyDown().getKeyCode() != 0) {
                    int stroke = event.getKeyDown().getKeyStroke();
                    for (JtvStatusItem t : items) {
                        if (t.getKeyStroke() != null && t.getKeyStroke().getKeyStroke() == stroke && commandEnabled(t.getCommand())) {
                            event.setWhat(evCommand);
                            event.getMessage().setCommand(t.getCommand());
                            event.getMessage().setInfoPtr(null);
                            return;
                        }
                    }
                }
                break;
            case evBroadcast:
                if (event.getMessage().getCommand() == cmCommandSetChanged)
                    drawView();
                break;
        }
    }

    /**
     * Returns a context-sensitive hint string for display after the status
     * items. The base implementation always returns an empty string.
     * Subclasses should override this to provide meaningful hints based on
     * {@code aHelpCtx}.
     *
     * @param aHelpCtx the current help context of the focused view
     * @return a hint string, or an empty string for no hint
     */
    public String hint(int aHelpCtx) {
        return "";
    }

    /**
     * Checks whether the focused view's help context has changed since the
     * last call. If so, calls {@link #findItems()} to re-select the active
     * item list and redraws the status line.
     * <p>
     * This method is called from the application's idle loop
     * ({@code TProgram.idle()}) after each event.
     */
    public void update() {
        JtvView p = topView();
        int h = (p != null) ? p.getHelpCtx() : hcNoContext;
        if (helpCtx != h) {
            helpCtx = h;
            findItems();
            drawView();
        }
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;
import static org.viktor44.jtvision.core.ViewFlags.sfShadow;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.StringUtils;

/**
 * A vertical drop-down or pop-up menu box.
 * <p>
 * JtvMenuBox renders a bordered box containing the items of a {@link JtvMenu}.
 * Each item is drawn on its own row between a top and bottom border drawn
 * with Unicode box-drawing characters. Separator items are drawn as
 * horizontal rules using the left-tee ({@code ├}), dash ({@code ─}), and
 * right-tee ({@code ┤}) characters. An arrow ({@code ►}) is drawn to the
 * right of any submenu item.
 * 
 * <h3>Size adjustment</h3>
 * 
 * {@link #adjustBounds()} computes the required width (longest item label
 * plus optional {@link JtvMenuItem#param} text plus borders) and sets the
 * view size accordingly before the box is shown. This is called by
 * {@link JtvMenuView#execute()} when a submenu is opened.
 * 
 * <h3>Colour and state</h3>
 * 
 * The box state includes {@code sfShadow} so a drop shadow is drawn beneath
 * it. {@code ofPreProcess} is added to options so hot keys operate before
 * the focused view sees the event. The box uses the inherited
 * {@link JtvMenuView#cpMenuView} palette.
 *
 * @see JtvMenuView
 * @see JtvMenuBar
 * @see JtvMenuPopup
 */
public class JtvMenuBox extends JtvMenuView {

    /**
     * Six-entry colour palette identical to {@link JtvMenuView#cpMenuView},
     * stored here for completeness:
     * <ol>
     *   <li>{@code 2} — normal text.</li>
     *   <li>{@code 3} — disabled text.</li>
     *   <li>{@code 4} — shortcut letter.</li>
     *   <li>{@code 5} — selected shortcut.</li>
     *   <li>{@code 6} — selected normal.</li>
     *   <li>{@code 7} — selected disabled.</li>
     * </ol>
     */
    private static final int[] cpMenuBox = {2, 3, 4, 5, 6, 7};

    /**
     * Constructs a menu box with the given bounds, menu, and parent.
     * Sets {@code sfShadow} in the state so a drop shadow is rendered, and
     * adds {@code ofPreProcess} to options for hot-key pre-processing.
     *
     * @param bounds       the initial bounding rectangle (adjusted by
     *                     {@link #adjustBounds()} before display)
     * @param aMenu        the {@link JtvMenu} whose items to display
     * @param aParentMenu  the owning {@link JtvMenuView} (e.g. the bar), or
     *                     {@code null} for a standalone pop-up
     */
    public JtvMenuBox(JtvRect bounds, JtvMenu aMenu, JtvMenuView aParentMenu) {
        super(bounds, aMenu);
        parentMenu = aParentMenu;
        state |= sfShadow;
        options |= ofPreProcess;
    }

    /**
     * Computes and applies the minimum width and height needed to display
     * all items. The width is the maximum of {@code 10} and the longest
     * item's display width, which is:
     * {@code cstrLen(name) + 6 + (param.length() + 2 if present)}. The
     * height is the item count plus 2 for the top and bottom borders.
     * <p>
     * Must be called before the box is inserted into an owner group.
     * {@link JtvMenuView#execute()} calls this automatically.
     */
    public void adjustBounds() {
        if (menu == null) return;
        int maxWidth = 10;
        int count = 0;
        for (JtvMenuItem p : menu.getItems()) {
            count++;
            if (p.getName() != null) {
                int w = StringUtils.cstrLen(p.getName()) + 6;
                if (p.getParam() != null)
                    w += p.getParam().length() + 2;
                if (w > maxWidth) maxWidth = w;
            }
        }
        size = new JtvPoint(maxWidth, count + 2); // +2 for border
    }

    /**
     * Draws the complete menu box: top border, item rows, bottom border.
     * <p>
     * <b>Top border:</b> {@code ┌─ … ─┐}.<br>
     * <b>Item rows:</b>
     * <ul>
     *   <li>Separators are drawn as {@code ├─ … ─┤}.</li>
     *   <li>Command and submenu items are framed by {@code │} on each
     *       side. The label is rendered with hot-key highlighting via
     *       {@link JtvDrawBuffer#moveCStr}. The {@link JtvMenuItem#param}
     *       string, if present, is right-aligned. Submenu items show a
     *       {@code ►} glyph near the right edge.</li>
     * </ul>
     * <b>Bottom border:</b> {@code └─ … ─┘}.
     * <p>
     * Colours mirror {@link JtvMenuBar#draw()}: normal, selected, disabled,
     * and selected-disabled variants.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr cNormal = getColor(1), cNormalHot = getColor(3);
        JtvColorAttr cSelect = getColor(4), cSelectHot = getColor(6);
        JtvColorAttr cNormDisabled = getColor(2), cNormDisabledHot = getColor(2);
        JtvColorAttr cSelDisabled = getColor(5), cSelDisabledHot = getColor(5);
        int w = size.getX();

        // Top border
        b.moveChar(0, ' ', cNormal, w);
        b.putChar(0, '┌');
        b.moveChar(1, '─', cNormal, w - 2);
        b.putChar(w - 1, '┐');
        writeLine(0, 0, w, 1, b);

        // Menu items
        int y = 1;
        if (menu != null) {
            for (JtvMenuItem p : menu.getItems()) {
                if (y >= size.getY() - 1) {
                    break;
                }
                b.moveChar(0, ' ', cNormal, w);
                if (p.getName() == null) {
                    // Separator
                    b.putChar(0, '├');
                    b.moveChar(1, '─', cNormal, w - 2);
                    b.putChar(w - 1, '┤');
                } else {
                    b.putChar(0, '│');
                    b.putChar(w - 1, '│');

                    JtvColorAttr color, colorHot;
                    if (p.isDisabled()) {
                        if (p == current) { color = cSelDisabled; colorHot = cSelDisabledHot; }
                        else { color = cNormDisabled; colorHot = cNormDisabledHot; }
                    } else {
                        if (p == current) { color = cSelect; colorHot = cSelectHot; }
                        else { color = cNormal; colorHot = cNormalHot; }
                    }

                    b.moveChar(1, ' ', color, w - 2);
                    b.moveCStr(3, p.getName(), color, colorHot);
                    if (p.getParam() != null)
                        b.moveStr(w - p.getParam().length() - 3, p.getParam(), color);
                    if (p.getCommand() == 0 && p.getSubMenu() != null)
                        b.putChar(w - 3, '►'); // submenu arrow
                }
                writeLine(0, y, w, 1, b);
                y++;
            }
        }

        // Bottom border
        b.moveChar(0, ' ', cNormal, w);
        b.putChar(0, '└');
        b.moveChar(1, '─', cNormal, w - 2);
        b.putChar(w - 1, '┘');
        writeLine(0, size.getY() - 1, w, 1, b);
    }

    /**
     * Returns the bounding rectangle of {@code item} within the box.
     * Items are laid out one per row starting at row 1 (after the top
     * border), spanning columns 1 through {@code size.x - 1}.
     *
     * @param item the menu item to locate
     * @return the item's rectangle in view-local coordinates, or
     *         {@code (1,1)-(size.x-1,2)} as a fallback if not found
     */
    @Override
    public JtvRect getItemRect(JtvMenuItem item) {
        int y = 1;
        if (menu != null) {
            for (JtvMenuItem p : menu.getItems()) {
                if (p == item)
                    return new JtvRect(1, y, size.getX() - 1, y + 1);
                y++;
            }
        }
        return new JtvRect(1, 1, size.getX() - 1, 2);
    }
}

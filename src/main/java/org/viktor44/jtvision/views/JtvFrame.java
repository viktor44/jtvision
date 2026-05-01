/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmZoom;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evMouse;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseUp;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;
import static org.viktor44.jtvision.core.ViewFlags.dmDragGrow;
import static org.viktor44.jtvision.core.ViewFlags.dmDragGrowLeft;
import static org.viktor44.jtvision.core.ViewFlags.dmDragMove;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.ofFramed;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfDragging;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;
import static org.viktor44.jtvision.core.ViewFlags.wfClose;
import static org.viktor44.jtvision.core.ViewFlags.wfGrow;
import static org.viktor44.jtvision.core.ViewFlags.wfMove;
import static org.viktor44.jtvision.core.ViewFlags.wfZoom;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;

/**
 * The border and title-bar view drawn around a {@link JtvWindow}.
 * <p>
 * JtvFrame renders a full-perimeter border using Unicode box-drawing characters.
 * The border uses single-line characters when the window is inactive or
 * being dragged, and double-line characters when active. Mixed-weight junction
 * glyphs (e.g. {@code ╟} / {@code ╤}) are used where {@code ofFramed}
 * subviews intersect the frame.
 * 
 * <h3>Title bar row (row 0)</h3>
 * 
 * The top row contains:
 * <ul>
 *   <li>The close button ({@code [■]}) at columns 2–4 when {@code wfClose}
 *       is set and the window is active.</li>
 *   <li>The window title centred in the available space.</li>
 *   <li>The window number (e.g. {@code 1}) near the right edge when the
 *       window is numbered.</li>
 *   <li>The zoom button ({@code [↑]} or {@code [↓]}) at the right edge
 *       when {@code wfZoom} is set and the window is active.</li>
 * </ul>
 * 
 * <h3>Bottom row</h3>
 * 
 * When the window is active and {@code wfGrow} is set, drag-corner icons
 * ({@code └─} / {@code ─┘}) are placed at the bottom-left and bottom-right
 * corners to indicate the resize handles.
 * 
 * <h3>Mouse handling</h3>
 * 
 * <ul>
 *   <li>Clicking the close button (or double-clicking the title bar) sends
 *       {@code cmClose}.</li>
 *   <li>Clicking the zoom button or double-clicking the top row sends
 *       {@code cmZoom}.</li>
 *   <li>Dragging the title bar moves the window ({@code dmDragMove}).</li>
 *   <li>Dragging the right resize corner grows from the right
 *       ({@code dmDragGrow}); dragging the left corner grows from the left
 *       ({@code dmDragGrowLeft}).</li>
 * </ul>
 * 
 * <h3>Colour palette</h3>
 * 
 * The five-entry palette {@link #cpFrame} maps to:
 * <ol>
 *   <li>Inactive frame colour.</li>
 *   <li>Inactive title colour.</li>
 *   <li>Active frame colour.</li>
 *   <li>Active title colour.</li>
 *   <li>Drag/resize icon colour.</li>
 * </ol>
 *
 * @see JtvWindow
 */
public class JtvFrame extends JtvView {

    /**
     * Per-cell direction bitmask table indexed by row/position (0–17).
     * <p>
     * Entries 0–8 are used for inactive and dragging windows (single-line);
     * entries 9–17 for active windows (double-line, bit {@code 0x10} set).
     * The low nibble encodes connection directions:
     * {@code 0x01}=up, {@code 0x02}=right, {@code 0x04}=down, {@code 0x08}=left.
     * The {@code 0x10} bit selects the double-line (active) style for the main
     * frame lines, while subview connector bits use only the low nibble.
     */
    private static final int[] initFrame = {
        // Offsets 0..8: inactive / dragging (single-line)
        0x06, 0x0A, 0x0C,
        0x05, 0x00, 0x05,
        0x03, 0x0A, 0x09,
        // Offsets 9..17: active (double-line, 0x10 bit set)
        0x16, 0x1A, 0x1C,
        0x15, 0x00, 0x15,
        0x13, 0x1A, 0x19
    };

    /**
     * 32-entry glyph lookup table indexed by a 5-bit direction code
     * ({@code main-double | left | down | right | up}).
     * <p>
     * Entries 0–15 produce single-line box-drawing characters; entries 16–31
     * produce double-line main-frame characters with optional single-line
     * branches for subview junctions (e.g. {@code ╟}, {@code ╤}, {@code ╢},
     * {@code ╧}).
     */
    private static final char[] frameChars = {
        ' ',	' ',    ' ',	'└', // 0..3:  _ _ _ └
        ' ',    '│', 	'┌',	'├', // 4..7:  _ │ ┌ ├
        ' ',    '┘', 	'─', 	'┴', // 8..11: _ ┘ ─ ┴
        '┐', 	'┤', 	'┬',	'┼', // 12..15: ┐ ┤ ┬ ┼
        ' ',    ' ',    ' ',    '╚', // 16..19: _ _ _ ╚
        ' ',    '║', 	'╔', 	'╟', // 20..23: _ ║ ╔ ╟
        ' ',    '╝', 	'═', 	'╧', // 24..27: _ ╝ ═ ╧
        '╗', 	'╢', 	'╤', 	' '  // 28..31: ╗ ╢ ╤ _
    };

    /** Title bar close button icon ({@code [■]}), with tilde hot-key markers. */
    private static final String closeIcon = "[~■~]";

    /** Zoom button icon when the window is not at maximum size ({@code [↑]}). */
    private static final String zoomIcon = "[~↑~]";

    /** Zoom button icon when the window is at maximum size ({@code [↓]}). */
    private static final String unZoomIcon = "[~\u2195~]"; //"[~↓~]";

    /** Right drag-corner icon ({@code ─┘}) placed at the bottom-right. */
    private static final String dragIcon = "~─┘~";

    /** Left drag-corner icon ({@code └─}) placed at the bottom-left. */
    private static final String dragLeftIcon = "~└─~";

    /**
     * Five-entry colour palette:
     * <ol>
     *   <li>{@code 1} — inactive frame.</li>
     *   <li>{@code 1} — inactive title.</li>
     *   <li>{@code 2} — active frame.</li>
     *   <li>{@code 2} — active title.</li>
     *   <li>{@code 3} — drag/zoom icons (active only).</li>
     * </ol>
     */
    private static final JtvPalette cpFrame = new JtvPalette(
    		new int[] {1, 1, 2, 2, 3}
    );

    /**
     * Constructs a frame that tracks the full width and height of its owner.
     * Grows with both dimensions ({@code gfGrowHiX | gfGrowHiY}) and
     * subscribes to broadcast and mouse-up events.
     *
     * @param bounds the initial bounding rectangle (the owner window's extent)
     */
    public JtvFrame(JtvRect bounds) {
        super(bounds);
        growMode = gfGrowHiX | gfGrowHiY;
        eventMask |= evBroadcast | evMouseUp;
    }

    /**
     * Draws the complete window frame for all rows.
     * <p>
     * Selects the colour scheme and frame-character set based on whether
     * the window is active, inactive, or being dragged. Draws the top row
     * (title bar with optional close/zoom buttons and window number), all
     * middle side rows, and the bottom row (with optional drag-corner icons).
     * Subviews with {@code ofFramed} cause mixed-weight junction glyphs to
     * appear where their borders meet the frame.
     */
    @Override
    public void draw() {
        JtvColorAttr cFrame, cFrameHot, cTitle;
        int f;

        if ((state & sfDragging) != 0) {
            cFrame = getColor(5); cFrameHot = getColor(5);
            cTitle = getColor(5);
            f = 0;
        }
        else if ((state & sfActive) == 0) {
            cFrame = getColor(1); cFrameHot = getColor(1);
            cTitle = getColor(2);
            f = 0;
        }
        else {
            cFrame = getColor(3); cFrameHot = getColor(5);
            cTitle = getColor(4);
            f = 9;
        }

        int width = size.getX();
        int l = width - 10;

        JtvWindow win = (owner instanceof JtvWindow) ? (JtvWindow) owner : null;
        if (win != null && (win.flags & (wfClose | wfZoom)) != 0) {
            l -= 6;
        }

        JtvDrawBuffer b = new JtvDrawBuffer();
        frameLine(b, 0, f, cFrame);

        if (win != null && win.number != wnNoNumber && win.number < 10) {
            l -= 4;
            int i = (win.flags & wfZoom) != 0 ? 7 : 3;
            b.putChar(width - i, (char) (win.number + '0'));
        }

        if (win != null) {
            String title = win.getTitle(l);
            if (title != null) {
                l = Math.min(title.length(), width - 10);
                l = Math.max(l, 0);
                int i = (width - l) >> 1;
                b.putChar(i - 1, ' ');
                b.moveStr(i, title, cTitle, l, 0);
                b.putChar(i + l, ' ');
            }
        }

        if ((state & sfActive) != 0 && win != null) {
            if ((win.flags & wfClose) != 0) {
                b.moveCStr(2, closeIcon, cFrame, cFrameHot);
            }
            if ((win.flags & wfZoom) != 0) {
                if (owner.size.equals(owner.getMaximumSize())) {
                    b.moveCStr(width - 5, unZoomIcon, cFrame, cFrameHot);
                }
                else {
                    b.moveCStr(width - 5, zoomIcon, cFrame, cFrameHot);
                }
            }
        }

        writeLine(0, 0, size.getX(), 1, b);

        for (int i = 1; i <= size.getY() - 2; i++) {
            frameLine(b, i, f + 3, cFrame);
            writeLine(0, i, size.getX(), 1, b);
        }

        frameLine(b, size.getY() - 1, f + 6, cFrame);
        if ((state & sfActive) != 0 && win != null && (win.flags & wfGrow) != 0) {
            b.moveCStr(0, dragLeftIcon, cFrame, cFrameHot);
            b.moveCStr(width - 2, dragIcon, cFrame, cFrameHot);
        }
        writeLine(0, size.getY() - 1, size.getX(), 1, b);
    }

    /**
     * Fills one row of the frame draw buffer with the appropriate border
     * characters and colour attribute.
     * <p>
     * The base characters come from the {@link #initFrame} direction table
     * at offsets {@code n}, {@code n+1}, and {@code n+2} (left edge, fill,
     * right edge). Any sibling subviews with {@code ofFramed} that intersect
     * row {@code y} contribute additional direction bits that cause
     * mixed-weight junction glyphs to be selected from {@link #frameChars}.
     *
     * @param frameBuf the draw buffer to write into
     * @param y        the frame-local row index
     * @param n        the starting offset into {@link #initFrame}
     * @param color    the resolved colour attribute to apply
     */
    private void frameLine(JtvDrawBuffer frameBuf, int y, int n, JtvColorAttr color) {
        if (size.getX() <= 0) {
        	return;
        }

        int[] frameMask = new int[size.getX()];
        frameMask[0] = initFrame[n];
        for (int x = 1; x < size.getX() - 1; x++) {
            frameMask[x] = initFrame[n + 1];
        }
        if (size.getX() > 1) {
            frameMask[size.getX() - 1] = initFrame[n + 2];
        }

        // Check framed subviews
        if (owner != null && owner instanceof JtvGroup) {
            JtvGroup grp = (JtvGroup) owner;
            if (grp.last != null) {
                JtvView v = grp.last.next;
                while (v != this) {
                    if ((v.options & ofFramed) != 0 && (v.state & sfVisible) != 0) {
                        int mask = 0;
                        if (y < v.origin.getY()) {
                            if (y == v.origin.getY() - 1) {
                                mask = 0x0A06;
                            }
                        }
                        else if (y < v.origin.getY() + v.size.getY()) {
                            mask = 0x0005;
                        }
                        else if (y == v.origin.getY() + v.size.getY()) {
                            mask = 0x0A03;
                        }

                        if (mask != 0) {
                            int start = Math.max(v.origin.getX(), 1);
                            int end = Math.min(v.origin.getX() + v.size.getX(), size.getX() - 1);
                            if (start < end) {
                                int maskLow = mask & 0x00FF;
                                int maskHigh = (mask >> 8) & 0xFF;
                                frameMask[start - 1] |= maskLow;
                                frameMask[end] |= maskLow ^ maskHigh;
                                if (maskLow != 0) {
                                    for (int x = start; x < end; x++) {
                                        frameMask[x] |= maskHigh;
                                    }
                                }
                            }
                        }
                    }
                    v = v.next;
                }
            }
        }

        for (int x = 0; x < size.getX(); x++) {
            int idx = frameMask[x] & 0x1F;
            frameBuf.putChar(x, frameChars[idx]);
            frameBuf.putAttribute(x, color);
        }
    }

    /**
     * Returns the frame colour palette {@link #cpFrame}.
     *
     * @return the frame's colour palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpFrame;
    }

    /**
     * Initiates an interactive drag/resize of the owning window.
     * Retrieves the drag limits from the owner's owner, and the min/max
     * size from the owner's {@link JtvView#getMinimumSize()} and {@link JtvView#getMaximumSize()},
     * then calls {@link JtvView#dragView(JtvEvent, int, JtvRect, JtvPoint, JtvPoint)}.
     *
     * @param event the initiating mouse-down event
     * @param mode  drag mode flags ({@code dmDragMove}, {@code dmDragGrow},
     *              or {@code dmDragGrowLeft})
     */
    public void dragWindow(JtvEvent event, int mode) {
        JtvRect limits = owner.owner.getExtent();
        owner.dragView(event, owner.dragMode | mode, limits, owner.getMinimumSize(), owner.getMaximumSize());
        clearEvent(event);
    }

    /**
     * Handles mouse interactions with the frame:
     * <ul>
     *   <li>Click on close button → sends {@code cmClose}.</li>
     *   <li>Click on zoom button or double-click on title row → sends
     *       {@code cmZoom}.</li>
     *   <li>Drag on title row → calls {@link #dragWindow} with
     *       {@code dmDragMove}.</li>
     *   <li>Drag on right resize corner → calls {@link #dragWindow} with
     *       {@code dmDragGrow}.</li>
     *   <li>Drag on left resize corner → calls {@link #dragWindow} with
     *       {@code dmDragGrowLeft}.</li>
     * </ul>
     * Also redisplays the frame when the active or dragging state changes.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        JtvWindow win = (owner instanceof JtvWindow) ? (JtvWindow) owner : null;

        if (event.getWhat() == evMouseDown && win != null) {
            JtvPoint mouse = makeLocal(event.getMouse().getWhere());
            if (mouse.getY() == 0) {
                if ((win.flags & wfClose) != 0
                		&& (state & sfActive) != 0
                		&& mouse.getX() >= 2 && mouse.getX() <= 4) {
                    while (mouseEvent(event, evMouse)) ;
                    mouse = makeLocal(event.getMouse().getWhere());
                    if (mouse.getY() == 0 && mouse.getX() >= 2 && mouse.getX() <= 4) {
                        event.setWhat(evCommand);
                        event.getMessage().setCommand(cmClose);
                        event.getMessage().setInfoPtr(owner);
                        putEvent(event);
                        clearEvent(event);
                    }
                }
                else if ((win.flags & wfZoom) != 0
                		&& (state & sfActive) != 0
                		&& ((mouse.getX() >= size.getX() - 5 && mouse.getX() <= size.getX() - 3)
                				|| (event.getMouse().getEventFlags() & meDoubleClick) != 0)) {
                    event.setWhat(evCommand);
                    event.getMessage().setCommand(cmZoom);
                    event.getMessage().setInfoPtr(owner);
                    putEvent(event);
                    clearEvent(event);
                }
                else if ((win.flags & wfMove) != 0) {
                    dragWindow(event, dmDragMove);
                }
            }
            else if ((state & sfActive) != 0
            		&& mouse.getY() >= size.getY() - 1
            		&& (win.flags & wfGrow) != 0) {
                if (mouse.getX() >= size.getX() - 2) {
                    dragWindow(event, dmDragGrow);
                }
                else if (mouse.getX() <= 1) {
                    dragWindow(event, dmDragGrowLeft);
                }
            }
        }
    }

    /**
     * Redraws the frame whenever the {@code sfActive} or {@code sfDragging}
     * state changes to switch between single-line and double-line border styles.
     *
     * @param aState the state bits being changed
     * @param enable {@code true} if the bits are being set
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & (sfActive | sfDragging)) != 0) {
            drawView();
        }
    }
}

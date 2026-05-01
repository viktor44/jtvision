/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.help;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvScroller;

/**
 * A scrollable viewer that renders a single {@link JtvHelpTopic}.
 * <p>
 * JtvHelpViewer extends {@link JtvScroller} to display the word-wrapped text of a
 * help topic and to highlight any embedded cross-references (hyperlinks).
 * It is the primary content view inside a {@link JtvHelpWindow}.
 * 
 * <h3>Cross-reference navigation</h3>
 * 
 * Cross-references are rendered with a distinct colour. The user can cycle
 * through them with Tab (forward) and Shift-Tab (backward). Pressing Enter
 * on the selected cross-reference, or clicking one with the mouse, calls
 * {@link #switchToTopic(int)} with the linked context ID, replacing the
 * displayed topic in place.
 * 
 * <h3>Keyboard shortcuts</h3>
 * 
 * <ul>
 *   <li><b>Tab</b> — select next cross-reference.</li>
 *   <li><b>Shift-Tab</b> — select previous cross-reference.</li>
 *   <li><b>Enter</b> — follow the currently selected cross-reference.</li>
 *   <li><b>Escape</b> — synthesises {@code cmClose} to close the help window.</li>
 * </ul>
 * 
 * <h3>Colour palette</h3>
 * 
 * The three-entry palette {@link #cpHelpViewer} maps to:
 * <ol>
 *   <li>Normal text colour.</li>
 *   <li>Cross-reference (keyword) colour.</li>
 *   <li>Selected cross-reference colour.</li>
 * </ol>
 *
 * @see JtvHelpTopic
 * @see JtvHelpFile
 * @see JtvHelpWindow
 */
public class JtvHelpViewer extends JtvScroller {

    /** The help file used to look up topics when following cross-references. */
    private JtvHelpFile hFile;

    /** The topic currently being displayed. */
    private JtvHelpTopic topic;

    /**
     * 1-based index of the currently highlighted cross-reference.
     * A value of {@code 1} selects the first cross-reference.
     */
    private int selected;

    /**
     * Default colour palette for the help viewer:
     * <ol>
     *   <li>{@code 0x13} — normal text.</li>
     *   <li>{@code 0x14} — cross-reference (keyword) text.</li>
     *   <li>{@code 0x15} — selected cross-reference text.</li>
     * </ol>
     */
    private static final JtvPalette cpHelpViewer = new JtvPalette(new int[] {0x13, 0x14, 0x15});

    /**
     * Constructs a help viewer, loads the initial topic for {@code context},
     * and configures the scroll limits.
     * <p>
     * Sets {@code ofSelectable} so the viewer can receive focus and keyboard
     * input. If {@code aHelpFile} is {@code null}, an empty topic is shown.
     *
     * @param bounds       the bounding rectangle of the viewer
     * @param aHScrollBar  the horizontal scroll bar, or {@code null}
     * @param aVScrollBar  the vertical scroll bar, or {@code null}
     * @param aHelpFile    the help file to load topics from, or {@code null}
     * @param context      the initial help-context ID to display
     */
    public JtvHelpViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvHelpFile aHelpFile, int context) {
        super(bounds, aHScrollBar, aVScrollBar);
        options |= ofSelectable;
        hFile = aHelpFile;
        topic = aHelpFile != null ? aHelpFile.getTopic(context) : JtvHelpTopic.fromText("");
        topic.setWidth(size.getX());
        setLimit(topic.longestLineWidth(), topic.numLines());
        selected = 1;
    }

    /**
     * Adjusts the topic word-wrap width to the new view width and resets the
     * scroll limits whenever the viewer's bounds change.
     *
     * @param bounds the new bounding rectangle
     */
    @Override
    public void changeBounds(JtvRect bounds) {
        super.changeBounds(bounds);
        topic.setWidth(size.getX());
        setLimit(topic.longestLineWidth(), topic.numLines());
    }

    /**
     * Renders the visible portion of the current topic.
     * <p>
     * Each row displays the corresponding word-wrapped line of the topic,
     * scrolled by the current {@code delta} offset. After drawing normal text,
     * the method overlays the cross-reference colour attributes: unselected
     * cross-references use the keyword colour; the selected one uses the
     * selected-keyword colour.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr normal = getColor(1);
        JtvColorAttr keyword = getColor(2);
        JtvColorAttr selKeyword = getColor(3);

        for (int i = 0; i < size.getY(); i++) {
            b.moveChar(0, ' ', normal, size.getX());
            String line = topic.getLine(i + delta.getY() + 1);
            if (line.length() > delta.getX()) {
                b.moveStr(0, line, normal, size.getX(), delta.getX());
            }
            for (int k = 0; k < topic.getNumCrossRefs(); k++) {
                JtvHelpTopic.CrossRef ref = topic.getCrossRef(k);
                if (ref != null && ref.getY() == i + delta.getY() + 1) {
                    int x = ref.getX() - delta.getX();
                    JtvColorAttr c = (k + 1) == selected ? selKeyword : keyword;
                    for (int j = 0; j < ref.getLength(); j++) {
                        int xx = x + j;
                        if (xx >= 0 && xx < size.getX()) {
                            b.putAttribute(xx, c);
                        }
                    }
                }
            }
            writeLine(0, i, size.getX(), 1, b);
        }
    }

    /**
     * Returns the colour palette {@link #cpHelpViewer}.
     *
     * @return the help viewer's colour palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpHelpViewer;
    }

    /**
     * Scrolls the view so that the cross-reference at zero-based index
     * {@code idx} is fully visible. If the reference is already in view,
     * no scrolling occurs.
     *
     * @param idx zero-based index of the cross-reference to reveal
     */
    private void makeSelectVisible(int idx) {
        JtvHelpTopic.CrossRef ref = topic.getCrossRef(idx);
        if (ref == null) {
            return;
        }
        int dx = delta.getX();
        if (ref.getX() < dx) dx = ref.getX();
        if (ref.getX() > dx + size.getX()) dx = ref.getX() - size.getX();
        int dy = delta.getY();
        if (ref.getY() <= dy) dy = ref.getY() - 1;
        if (ref.getY() > dy + size.getY()) dy = ref.getY() - size.getY();
        JtvPoint d = new JtvPoint(dx, dy);
        if (d.getX() != delta.getX() || d.getY() != delta.getY()) {
            scrollTo(d.getX(), d.getY());
        }
    }

    /**
     * Navigates to the topic identified by {@code keyRef}, replacing the
     * currently displayed topic.
     * <p>
     * The scroll position is reset to the top-left, the display width is
     * applied to the new topic, the scroll limits are updated, and the view
     * is redrawn.
     *
     * @param keyRef the help-context ID of the topic to display
     */
    public void switchToTopic(int keyRef) {
        topic = hFile.getTopic(keyRef);
        topic.setWidth(size.getX());
        scrollTo(0, 0);
        setLimit(topic.longestLineWidth(), topic.numLines());
        selected = 1;
        drawView();
    }

    /**
     * Handles keyboard, mouse, and command events for the help viewer.
     * <ul>
     *   <li><b>Tab</b> — advance {@link #selected} to the next cross-reference
     *       (wrapping around), scroll it into view, and redraw.</li>
     *   <li><b>Shift-Tab</b> — move {@link #selected} to the previous
     *       cross-reference (wrapping around), scroll it into view, and redraw.</li>
     *   <li><b>Enter</b> — follow the currently selected cross-reference by
     *       calling {@link #switchToTopic(int)}.</li>
     *   <li><b>Escape</b> — synthesises a {@code cmClose} command event.</li>
     *   <li><b>Mouse click</b> — if the click lands within a cross-reference
     *       span, selects and follows that cross-reference.</li>
     *   <li><b>{@code cmClose} / {@code cmCancel}</b> — ends the modal state
     *       when the owning group is modal.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        switch (event.getWhat()) {
            case evKeyDown:
                switch (event.getKeyDown().getKeyStroke()) {
                    case JtvKey.kbTab:
                        selected++;
                        if (selected > topic.getNumCrossRefs()) {
                            selected = 1;
                        }
                        if (topic.getNumCrossRefs() != 0) {
                            makeSelectVisible(selected - 1);
                        }
                        drawView();
                        clearEvent(event);
                        return;
                    case JtvKey.kbShiftTab:
                        selected--;
                        if (selected <= 0) {
                            selected = topic.getNumCrossRefs();
                        }
                        if (topic.getNumCrossRefs() != 0) {
                            makeSelectVisible(selected - 1);
                        }
                        drawView();
                        clearEvent(event);
                        return;
                    case JtvKey.kbEnter:
                        if (selected > 0 && selected <= topic.getNumCrossRefs()) {
                            JtvHelpTopic.CrossRef ref = topic.getCrossRef(selected - 1);
                            if (ref != null) {
                                switchToTopic(ref.getRef());
                            }
                        }
                        clearEvent(event);
                        return;
                    case JtvKey.kbEsc:
                        event.setWhat(evCommand);
                        event.getMessage().setCommand(cmClose);
                        putEvent(event);
                        clearEvent(event);
                        return;
                    default:
                        return;
                }

            case evMouseDown:
                JtvPoint mouse = makeLocal(event.getMouse().getWhere()).add(delta);
                for (int k = 0; k < topic.getNumCrossRefs(); k++) {
                    JtvHelpTopic.CrossRef ref = topic.getCrossRef(k);
                    if (ref != null && ref.getY() == mouse.getY() + 1 && mouse.getX() >= ref.getX() && mouse.getX() < ref.getX() + ref.getLength()) {
                        selected = k + 1;
                        drawView();
                        switchToTopic(ref.getRef());
                        clearEvent(event);
                        return;
                    }
                }
                return;

            case evCommand:
                if (event.getMessage().getCommand() == cmClose && (owner.getState() & org.viktor44.jtvision.core.ViewFlags.sfModal) != 0) {
                    endModal(cmClose);
                    clearEvent(event);
                }
                else if (event.getMessage().getCommand() == cmCancel && (owner.getState() & org.viktor44.jtvision.core.ViewFlags.sfModal) != 0) {
                    endModal(cmCancel);
                    clearEvent(event);
                }
                return;

            default:
                return;
        }
    }
}

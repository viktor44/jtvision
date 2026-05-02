/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.editors;

import static org.viktor44.jtvision.core.CommandCodes.cmBackSpace;
import static org.viktor44.jtvision.core.CommandCodes.cmCharLeft;
import static org.viktor44.jtvision.core.CommandCodes.cmCharRight;
import static org.viktor44.jtvision.core.CommandCodes.cmClear;
import static org.viktor44.jtvision.core.CommandCodes.cmCopy;
import static org.viktor44.jtvision.core.CommandCodes.cmCut;
import static org.viktor44.jtvision.core.CommandCodes.cmDelChar;
import static org.viktor44.jtvision.core.CommandCodes.cmDelEnd;
import static org.viktor44.jtvision.core.CommandCodes.cmDelLine;
import static org.viktor44.jtvision.core.CommandCodes.cmDelStart;
import static org.viktor44.jtvision.core.CommandCodes.cmDelWord;
import static org.viktor44.jtvision.core.CommandCodes.cmDelWordLeft;
import static org.viktor44.jtvision.core.CommandCodes.cmEncoding;
import static org.viktor44.jtvision.core.CommandCodes.cmFind;
import static org.viktor44.jtvision.core.CommandCodes.cmHideSelect;
import static org.viktor44.jtvision.core.CommandCodes.cmIndentMode;
import static org.viktor44.jtvision.core.CommandCodes.cmInsMode;
import static org.viktor44.jtvision.core.CommandCodes.cmLineDown;
import static org.viktor44.jtvision.core.CommandCodes.cmLineEnd;
import static org.viktor44.jtvision.core.CommandCodes.cmLineStart;
import static org.viktor44.jtvision.core.CommandCodes.cmLineUp;
import static org.viktor44.jtvision.core.CommandCodes.cmNewLine;
import static org.viktor44.jtvision.core.CommandCodes.cmPageDown;
import static org.viktor44.jtvision.core.CommandCodes.cmPageUp;
import static org.viktor44.jtvision.core.CommandCodes.cmPaste;
import static org.viktor44.jtvision.core.CommandCodes.cmReplace;
import static org.viktor44.jtvision.core.CommandCodes.cmSearchAgain;
import static org.viktor44.jtvision.core.CommandCodes.cmSelectAll;
import static org.viktor44.jtvision.core.CommandCodes.cmStartSelect;
import static org.viktor44.jtvision.core.CommandCodes.cmTextEnd;
import static org.viktor44.jtvision.core.CommandCodes.cmTextStart;
import static org.viktor44.jtvision.core.CommandCodes.cmUndo;
import static org.viktor44.jtvision.core.CommandCodes.cmUpdateTitle;
import static org.viktor44.jtvision.core.CommandCodes.cmWordLeft;
import static org.viktor44.jtvision.core.CommandCodes.cmWordRight;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseAuto;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfCursorIns;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvView;

/**
 * A simple, fast text editor view for use in JT Vision applications.
 * <p>
 * {@code JtvEditor} provides mouse support, undo, clipboard cut/copy/paste,
 * automatic indentation, insert/overwrite toggle, key binding, and search and replace.
 * It can be used both for editing text files and for multi-line memo fields in dialog boxes.
 * <p>
 * {@code JtvEditor} is the base class for {@link JtvMemo} (dialog memo fields) and
 * {@link JtvFileEditor} (file-backed editors).  All editing functionality is implemented
 * here; subclasses add persistence and data-transfer support.
 * <p>
 * The editor stores all text in a {@link StringBuilder} buffer.  Positions within the
 * text are expressed as zero-based character offsets into that buffer.
 * 
 * <h3>Colour palette</h3>
 * 
 * The two-entry palette {@link #cpEditor} maps to:
 * <ol>
 *   <li>Normal text colour.</li>
 *   <li>Selected text colour.</li>
 * </ol>
 */
public class JtvEditor extends JtvView {

    /**
     * Two-entry colour palette:
     * <ol>
     *   <li>{@code 6} — normal text colour.</li>
     *   <li>{@code 7} — selected text colour.</li>
     * </ol>
     */
    protected static final JtvPalette cpEditor = new JtvPalette(new int[] {6, 7});

    /** Horizontal scroll bar, or {@code null} if none. */
    protected JtvScrollBar hScrollBar;

    /** Vertical scroll bar, or {@code null} if none. */
    protected JtvScrollBar vScrollBar;

    /**
     * Line/column indicator view, or {@code null} if none.
     * Updated after every cursor movement via {@link JtvIndicator#setValue}.
     */
    protected JtvIndicator indicator;

    /** The text content being edited. */
    protected StringBuilder buffer = new StringBuilder();

    /**
     * Maximum size of the edit buffer in characters.
     * Passed to the constructor and used to cap buffer growth.
     */
    protected int bufSize;

    /**
     * Current number of characters in the buffer ({@code buffer.length()}).
     * Kept in sync by {@link #updateMetrics()}.
     */
    protected int bufLen;

    /**
     * Buffer offset of the start of the current selection.
     * When there is no selection, {@code selStart == selEnd}.
     */
    protected int selStart;

    /**
     * Buffer offset of the end of the current selection.
     * When there is no selection, {@code selStart == selEnd}.
     */
    protected int selEnd;

    /**
     * Buffer offset of the insertion cursor.
     * Either equals {@link #selStart} or {@link #selEnd} depending on the
     * direction in which the selection was extended.
     */
    protected int curPtr;

    /**
     * Column/line position of the cursor within the text.
     * {@code curPos.x} is the column (0-based); {@code curPos.y} is the line (0-based).
     */
    protected JtvPoint curPos = new JtvPoint();

    /**
     * The top-left offset of the visible area.
     * {@code delta.x} is the leftmost visible column; {@code delta.y} is the topmost visible line.
     */
    protected JtvPoint delta = new JtvPoint();

    /**
     * Maximum width and line count of the current text.
     * {@code limit.x} is the length of the longest line + 1; {@code limit.y} is the line count.
     * Used to configure scroll bar ranges.
     */
    protected JtvPoint limit = new JtvPoint(256, 1);

    /**
     * Buffer offsets of the start of each line.
     * {@code lineOffsets[i]} is the index in {@link #buffer} where line {@code i} begins.
     * Rebuilt by {@link #updateMetrics()} after every buffer change.
     */
    protected int[] lineOffsets = new int[]{0};

    /**
     * {@code true} if this view is in a valid state.
     * Returned by {@link #valid(int)} to indicate whether the editor can be used.
     */
    protected boolean isValid = true;

    /**
     * {@code true} if the editor supports single-level undo.
     * Set to {@code true} by the constructor; may be set to {@code false} to disable undo.
     */
    protected boolean canUndo = true;

    /**
     * {@code true} if the buffer has been modified since the last load or save.
     * Displayed in the {@link JtvIndicator} as an asterisk prefix.
     */
    protected boolean modified;

    /**
     * {@code true} while the user is in selection mode (e.g. after {@code cmStartSelect}
     * but before the selection is finalized).
     */
    protected boolean selecting;

    /**
     * {@code true} if the editor is in overwrite mode; {@code false} for insert mode.
     * Toggled by the Insert key or {@code cmInsMode} command.
     */
    protected boolean overwrite;

    /**
     * {@code true} if the editor automatically indents new lines to match the indentation
     * of the preceding line.  Toggled by the {@code cmIndentMode} command.
     */
    protected boolean autoIndent = true;


    /** Buffer offset where the last recorded change started. */
    protected int undoOffset;

    /** Text deleted at {@link #undoOffset} by the last recorded change (empty if none). */
    protected String undoDeletedText = "";

    /** Number of characters inserted at {@link #undoOffset} by the last recorded change. */
    protected int undoInsertedLength;

    /** Saved cursor position for undo. */
    protected int undoCurPtr;

    /** Saved selection start for undo. */
    protected int undoSelStart;

    /** Saved selection end for undo. */
    protected int undoSelEnd;

    /** {@code true} when a saved undo snapshot is available to restore. */
    protected boolean canUndoState;

    /** {@code true} if the editor should reject all modifications. */
    protected boolean readOnly;

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        updateCommands();
    }

    /**
     * Constructs a {@code TEditor} view.
     *
     * <p>Sets {@code options |= ofSelectable} and configures the event mask to receive
     * mouse, key, command, and broadcast events.  Associates the supplied scroll bars and
     * indicator, then shows the cursor and initialises the view metrics.
     *
     * @param bounds       the bounding rectangle for this editor view
     * @param hScrollBar  optional horizontal scroll bar, or {@code null}
     * @param vScrollBar  optional vertical scroll bar, or {@code null}
     * @param indicator   optional position indicator, or {@code null}
     * @param bufSize     initial buffer capacity (minimum {@code 0x1000})
     */
    public JtvEditor(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator, int bufSize) {
        super(bounds);
        
        this.hScrollBar = hScrollBar;
        this.vScrollBar = vScrollBar;
        this.indicator = indicator;
        this.bufSize = Math.max(0x1000, bufSize);
        this.options |= ofSelectable;
        this.eventMask = evMouseDown | evKeyDown | evCommand | evBroadcast;
        
        showCursor();
        updateMetrics();
        trackCursor(true);
    }

    /**
     * Returns the editor palette ({@code cpEditor}), mapping color index 1 to normal text
     * and color index 2 to selected text.
     *
     * @return the editor palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpEditor;
    }

    /**
     * Resizes the editor view to {@code bounds}, clamps the scroll offset so it remains
     * within the valid range, then redraws.
     *
     * @param bounds new bounding rectangle
     */
    @Override
    public void changeBounds(JtvRect bounds) {
        setBounds(bounds);
        delta = new JtvPoint(
        		Math.max(0, Math.min(delta.getX(), Math.max(0, limit.getX() - size.getX()))),
                Math.max(0, Math.min(delta.getY(), Math.max(0, limit.getY() - size.getY())))
        );
        drawView();
    }

    /**
     * Renders the visible portion of the editor content, applying the normal attribute to
     * unselected characters and the selected attribute to characters within the current
     * selection.  Positions the hardware cursor at the current cursor location.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr normal = getColor(1);
        JtvColorAttr selected = getColor(2);

        for (int y = 0; y < size.getY(); y++) {
            int lineIdx = delta.getY() + y;
            String line = getLine(lineIdx);
            int lineBase = lineIdx < lineOffsets.length ? lineOffsets[lineIdx] : buffer.length();
            b.moveChar(0, ' ', normal, size.getX());
            int x = 0;
            for (int i = delta.getX(); i < line.length() && x < size.getX(); i++, x++) {
                b.putChar(x, printableChar(line.charAt(i)));
                b.putAttribute(x, inSelection(lineBase + i) ? selected : normal);
            }
            writeBuf(0, y, size.getX(), 1, b);
        }
        setCursor(curPos.getX() - delta.getX(), curPos.getY() - delta.getY());
    }

    private static char printableChar(char ch) {
        if (ch < 0x20 || ch == 0x7F || (ch >= 0x80 && ch <= 0x9F)) {
            return ' ';
        }
        return ch;
    }

    /**
     * Recomputes {@link #bufLen}, {@link #limit}, and {@link #curPos} from the current
     * buffer content, then updates the scroll bars, the indicator, and the command states.
     * Must be called after any modification to {@link #buffer} or {@link #curPtr}.
     */
    protected void updateMetrics() {
        bufLen = buffer.length();
        int lines = 1;
        int maxWidth = 0;
        int col = 0;

        // Count lines and find max width in one pass; also build lineOffsets.
        // Pre-size generously and grow if needed to avoid an extra pass.
        int capacity = Math.max(1024, bufLen / 40);
        int[] offsets = new int[capacity];
        offsets[0] = 0;

        for (int i = 0; i < bufLen; i++) {
            char c = buffer.charAt(i);
            if (c == '\n') {
                if (col > maxWidth) {
                	maxWidth = col;
                }
                col = 0;
                if (lines >= offsets.length) {
                    offsets = Arrays.copyOf(offsets, offsets.length * 2);
                }
                offsets[lines] = i + 1;
                lines++;
            }
            else {
                col++;
            }
        }
        if (col > maxWidth) {
        	maxWidth = col;
        }

        lineOffsets = Arrays.copyOf(offsets, lines);
        limit = new JtvPoint(Math.max(256, maxWidth + 1), Math.max(1, lines));

        if (curPtr > bufLen) {
            curPtr = bufLen;
        }
        recalcCurPos();
        updateScrollBars();
        updateIndicator();
        updateCommands();
    }

    /**
     * Updates metrics incrementally after an edit at {@code editOffset} that replaced
     * {@code deletedLength} characters with {@code insertedLength} characters.
     * Runs in O(insertedLength + lines in affected region + log n) instead of O(bufLen).
     * The {@code lineOffsets} array must still reflect the state BEFORE the edit when this
     * is called — the buffer itself must already contain the new content.
     */
    protected void updateMetricsIncremental(int editOffset, int deletedLength, int insertedLength) {
        bufLen = buffer.length();
        int shift = insertedLength - deletedLength;

        // Line index of the line that contains (or starts at) editOffset.
        int lineContaining = Math.max(0, upperBound(editOffset) - 1);

        // Range of old line-start entries that fall inside the deleted region [editOffset, editOffset+deletedLength).
        // These entries are obsolete and must be removed.
        int firstRemoved = lineContaining + 1;
        int lastRemoved = firstRemoved;
        while (lastRemoved < lineOffsets.length && lineOffsets[lastRemoved] < editOffset + deletedLength) {
            lastRemoved++;
        }
        int removedCount = lastRemoved - firstRemoved;

        // Scan the inserted region in the (already-updated) buffer for new '\n' characters.
        int insertEnd = editOffset + insertedLength;
        int[] newStarts = new int[Math.max(1, insertedLength)];
        int newCount = 0;
        for (int i = editOffset; i < insertEnd; i++) {
            if (buffer.charAt(i) == '\n') {
                if (newCount == newStarts.length) {
                	newStarts = Arrays.copyOf(newStarts, newStarts.length * 2);
                }
                newStarts[newCount++] = i + 1;
            }
        }

        // Build the new lineOffsets: keep prefix, splice new starts, shift suffix.
        int oldLen = lineOffsets.length;
        int newLen = oldLen - removedCount + newCount;
        int[] newOffsets = new int[newLen];
        System.arraycopy(lineOffsets, 0, newOffsets, 0, firstRemoved);
        System.arraycopy(newStarts, 0, newOffsets, firstRemoved, newCount);
        int suffixDst = firstRemoved + newCount;
        for (int i = lastRemoved; i < oldLen; i++) {
            newOffsets[suffixDst++] = lineOffsets[i] + shift;
        }
        lineOffsets = newOffsets;

        // Update limit.y.
        int newLineCount = Math.max(1, lineOffsets.length);

        // Recompute limit.x for affected lines; keep existing value as lower bound
        // so that lines outside the affected region continue to influence the range.
        int newMax = limit.getX() - 1;
        int affectedEnd = Math.min(lineContaining + newCount + 2, lineOffsets.length);
        for (int li = lineContaining; li < affectedEnd; li++) {
            int w = getLineLength(li);
            if (w > newMax) {
            	newMax = w;
            }
        }
        limit = new JtvPoint(Math.max(256, newMax + 1), newLineCount);

        if (curPtr > bufLen) {
        	curPtr = bufLen;
        }
        recalcCurPos();
        updateScrollBars();
        updateIndicator();
        updateCommands();
    }

    /** Broadcasts {@code cmUpdateTitle} to the owner so the window frame refreshes its title. */
    protected void notifyTitleUpdate() {
        message(owner, evBroadcast, cmUpdateTitle, this);
    }

    /**
     * Records the delta of an edit operation so that {@link #undo()} can reverse it.
     * Only the changed region is stored, not the entire buffer.
     *
     * @param offset        buffer offset where the change starts
     * @param deletedText   text that will be (or was) deleted at {@code offset}
     * @param insertedLength number of characters that will be (or were) inserted at {@code offset}
     */
    protected void snapshotUndo(int offset, String deletedText, int insertedLength) {
        undoOffset = offset;
        undoDeletedText = deletedText != null ? deletedText : "";
        undoInsertedLength = insertedLength;
        undoCurPtr = curPtr;
        undoSelStart = selStart;
        undoSelEnd = selEnd;
        canUndoState = true;
    }

    private void recalcCurPos() {
        int p = Math.max(0, Math.min(curPtr, bufLen));
        int line = Math.max(0, upperBound(p) - 1);
        curPos = new JtvPoint(p - lineOffsets[line], line);
    }

    // Returns the first index i where lineOffsets[i] > value (standard upper_bound).
    private int upperBound(int value) {
        int lo = 0, hi = lineOffsets.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (lineOffsets[mid] <= value) {
            	lo = mid + 1;
            }
            else hi = mid;
        }
        return lo;
    }

    // Returns the column-width of line lineIndex without allocating a String.
    private int getLineLength(int lineIndex) {
        if (lineIndex < 0 || lineIndex >= lineOffsets.length) {
        	return 0;
        }
        int start = lineOffsets[lineIndex];
        int end = start;
        int len = buffer.length();
        while (end < len && buffer.charAt(end) != '\n') {
        	end++;
        }
        return end - start;
    }

    private void updateScrollBars() {
        if (hScrollBar != null) {
            hScrollBar.setParams(delta.getX(), 0, Math.max(0, limit.getX() - size.getX()), Math.max(1, size.getX() / 2), 1);
        }
        if (vScrollBar != null) {
            vScrollBar.setParams(delta.getY(), 0, Math.max(0, limit.getY() - size.getY()), Math.max(1, size.getY() - 1), 1);
        }
    }

    private void updateIndicator() {
        if (indicator != null) {
            indicator.setValue(curPos, modified);
        }
    }

    private boolean inSelection(int i) {
        int a = Math.min(selStart, selEnd);
        int b = Math.max(selStart, selEnd);
        return i >= a && i < b;
    }

    /**
     * Returns the text content of the line at the given 0-based line index.
     * The returned string does not include the terminating newline character.
     *
     * @param index 0-based line index
     * @return line content, or an empty string if the index is out of range
     */
    protected String getLine(int index) {
        if (index < 0 || index >= lineOffsets.length) {
            return "";
        }
        int start = lineOffsets[index];
        int end = start;
        int len = buffer.length();
        while (end < len && buffer.charAt(end) != '\n') {
            end++;
        }
        return buffer.substring(start, end);
    }

    /**
     * Converts a (line, column) coordinate to a buffer offset.
     *
     * @param lineIndex 0-based line index
     * @param col       0-based column index within the line
     * @return buffer offset, clamped to {@code [0, buffer.length()]}
     */
    protected int lineToPtr(int lineIndex, int col) {
        if (lineIndex < 0) {
            return 0;
        }
        int start = lineIndex < lineOffsets.length ? lineOffsets[lineIndex] : buffer.length();
        int p = start;
        int len = buffer.length();
        while (col > 0 && p < len && buffer.charAt(p) != '\n') {
            p++;
            col--;
        }
        return p;
    }

    /**
     * Returns the buffer offset of the first character of the line containing {@code ptr}.
     *
     * @param ptr a buffer offset
     * @return offset of the start of the line
     */
    protected int lineStart(int ptr) {
        ptr = Math.max(0, Math.min(ptr, buffer.length()));
        return lineOffsets[Math.max(0, upperBound(ptr) - 1)];
    }

    /**
     * Returns the buffer offset of the newline character (or end of buffer) that terminates
     * the line containing {@code ptr}.
     *
     * @param ptr a buffer offset
     * @return offset of the end of the line (the {@code '\n'} or {@code buffer.length()})
     */
    protected int lineEnd(int ptr) {
        ptr = Math.max(0, Math.min(ptr, buffer.length()));
        int k = Math.max(0, upperBound(ptr) - 1);
        return k + 1 < lineOffsets.length ? lineOffsets[k + 1] - 1 : buffer.length();
    }

    /**
     * Returns the buffer offset of the first character of the line following the line
     * containing {@code ptr}.  If already on the last line, returns the end of the buffer.
     *
     * @param ptr a buffer offset
     * @return offset of the start of the next line
     */
    protected int nextLine(int ptr) {
        int k = Math.max(0, upperBound(ptr) - 1);
        return k + 1 < lineOffsets.length ? lineOffsets[k + 1] : buffer.length();
    }

    /**
     * Returns the buffer offset of the first character of the line preceding the line
     * containing {@code ptr}.  If already on the first line, returns {@code 0}.
     *
     * @param ptr a buffer offset
     * @return offset of the start of the previous line
     */
    protected int prevLine(int ptr) {
        int k = Math.max(0, upperBound(ptr) - 1);
        return k > 0 ? lineOffsets[k - 1] : 0;
    }

    /**
     * Returns the buffer offset of the character immediately after {@code ptr}, clamped to
     * the end of the buffer.
     *
     * @param ptr a buffer offset
     * @return {@code ptr + 1}, clamped to {@code buffer.length()}
     */
    protected int nextChar(int ptr) {
        return Math.min(buffer.length(), ptr + 1);
    }

    /**
     * Returns the buffer offset of the character immediately before {@code ptr}, clamped to
     * the start of the buffer.
     *
     * @param ptr a buffer offset
     * @return {@code ptr - 1}, clamped to {@code 0}
     */
    protected int prevChar(int ptr) {
        return Math.max(0, ptr - 1);
    }

    /**
     * Returns the buffer offset of the start of the next word after {@code ptr}.
     * Skips over the current word (letter/digit run), then skips over any non-word characters.
     *
     * @param ptr a buffer offset
     * @return offset of the start of the next word, or end-of-buffer
     */
    protected int nextWord(int ptr) {
        while (ptr < buffer.length() && Character.isLetterOrDigit(buffer.charAt(ptr))) {
            ptr++;
        }
        while (ptr < buffer.length() && !Character.isLetterOrDigit(buffer.charAt(ptr))) {
            ptr++;
        }
        return ptr;
    }

    /**
     * Returns the buffer offset of the start of the word preceding {@code ptr}.
     * Skips backwards over non-word characters, then backwards over the word itself.
     *
     * @param ptr a buffer offset
     * @return offset of the start of the previous word, or {@code 0}
     */
    protected int prevWord(int ptr) {
        ptr = Math.max(0, ptr - 1);
        while (ptr > 0 && !Character.isLetterOrDigit(buffer.charAt(ptr))) {
            ptr--;
        }
        while (ptr > 0 && Character.isLetterOrDigit(buffer.charAt(ptr - 1))) {
            ptr--;
        }
        return ptr;
    }

    /**
     * Returns {@code true} if the editor currently has a non-empty text selection.
     *
     * @return {@code true} if {@code selStart != selEnd}
     */
    protected boolean hasSelection() {
        return selStart != selEnd;
    }

    /**
     * Sets the selection range and cursor position without recording an undo snapshot.
     *
     * @param a          selection start (smaller offset)
     * @param b          selection end (larger offset)
     * @param curAtStart {@code true} to place the cursor at the start of the selection,
     *                   {@code false} to place it at the end
     */
    protected void setSelect(int a, int b, boolean curAtStart) {
        selStart = Math.max(0, Math.min(a, buffer.length()));
        selEnd = Math.max(0, Math.min(b, buffer.length()));
        curPtr = curAtStart ? selStart : selEnd;
        recalcCurPos();
        trackCursor(false);
        updateCommands();
    }

    /**
     * Moves the cursor to buffer offset {@code p} and optionally extends the selection.
     *
     * @param p          target buffer offset
     * @param selectMode bit 0 set means extend the selection; bit 0 clear collapses it
     */
    protected void setCurPtr(int p, int selectMode) {
        p = Math.max(0, Math.min(p, buffer.length()));
        if ((selectMode & 0x01) == 0) {
            selStart = p;
            selEnd = p;
            curPtr = p;
        }
        else {
            if (curPtr == selStart) {
                selStart = p;
                curPtr = p;
            }
            else {
                selEnd = p;
                curPtr = p;
            }
        }
        recalcCurPos();
        updateCommands();
    }

    /**
     * Deletes the currently selected text, records an undo snapshot, and updates metrics.
     * Does nothing if there is no selection.
     */
    protected void deleteSelect() {
        if (!hasSelection()) {
            return;
        }
        int a = Math.min(selStart, selEnd);
        int b = Math.max(selStart, selEnd);
        snapshotUndo(a, buffer.substring(a, b), 0);
        buffer.delete(a, b);
        curPtr = a;
        selStart = selEnd = curPtr;
        modified = true;
        notifyTitleUpdate();
        canUndo = true;
        updateMetricsIncremental(a, b - a, 0);
    }

    /**
     * Inserts {@code s} at the cursor position, replacing any current selection first.
     * Records a single combined undo delta covering both the selection deletion and the insertion.
     *
     * @param s          text to insert
     * @param selectText {@code true} to select the newly inserted text after insertion
     */
    protected void insertText(String s, boolean selectText) {
        if (s == null || s.isEmpty()) {
            return;
        }
        int insertPos = curPtr;
        String deletedText = "";
        if (hasSelection()) {
            int a = Math.min(selStart, selEnd);
            int b = Math.max(selStart, selEnd);
            deletedText = buffer.substring(a, b);
            insertPos = a;
            buffer.delete(a, b);
            curPtr = a;
            selStart = selEnd = a;
        }
        int deletedLen = deletedText.length();
        snapshotUndo(insertPos, deletedText, s.length());
        buffer.insert(insertPos, s);
        int start = insertPos;
        curPtr = insertPos + s.length();
        if (selectText) {
            selStart = start;
            selEnd = curPtr;
        }
        else {
            selStart = selEnd = curPtr;
        }
        modified = true;
        notifyTitleUpdate();
        canUndo = true;
        updateMetricsIncremental(insertPos, deletedLen, s.length());
        trackCursor(false);
    }

    /**
     * Deletes the characters in the range {@code [a, b)} from the buffer, records an
     * undo snapshot, and updates metrics.  The range is clamped to valid buffer boundaries.
     *
     * @param a start offset (inclusive)
     * @param b end offset (exclusive)
     */
    protected void deleteRange(int a, int b) {
        a = Math.max(0, Math.min(a, buffer.length()));
        b = Math.max(0, Math.min(b, buffer.length()));
        if (b <= a) {
            return;
        }
        snapshotUndo(a, buffer.substring(a, b), 0);
        buffer.delete(a, b);
        curPtr = a;
        selStart = selEnd = curPtr;
        modified = true;
        notifyTitleUpdate();
        canUndo = true;
        updateMetricsIncremental(a, b - a, 0);
    }

    /**
     * Inserts a newline at the cursor position.  If {@link #autoIndent} is {@code true},
     * the new line is automatically indented to match the leading whitespace of the
     * current line.
     */
    protected void newLine() {
        String indent = "";
        if (autoIndent) {
            int s = lineStart(curPtr);
            int e = s;
            while (e < buffer.length()) {
                char c = buffer.charAt(e);
                if (c == ' ' || c == '\t') {
                    e++;
                }
                else {
                    break;
                }
            }
            indent = buffer.substring(s, e);
        }
        insertText("\n" + indent, false);
    }

    /**
     * Scrolls the view so that column {@code x} and line {@code y} are at the top-left
     * of the visible area.  Values are clamped to the valid scroll range.  Redraws if
     * the scroll position actually changed.
     *
     * @param x target horizontal scroll offset (column)
     * @param y target vertical scroll offset (line)
     */
    protected void scrollTo(int x, int y) {
        x = Math.max(0, Math.min(x, Math.max(0, limit.getX() - size.getX())));
        y = Math.max(0, Math.min(y, Math.max(0, limit.getY() - size.getY())));
        if (x != delta.getX() || y != delta.getY()) {
            delta = new JtvPoint(x, y);
            updateScrollBars();
            drawView();
        }
    }

    /**
     * Scrolls the view to ensure the cursor is visible, then updates the indicator.
     *
     * @param center {@code true} to center the cursor vertically; {@code false} to do the
     *               minimum scroll necessary to keep the cursor in view
     */
    protected void trackCursor(boolean center) {
        if (center) {
            scrollTo(curPos.getX() - size.getX() + 1, curPos.getY() - size.getY() / 2);
        }
        else {
            int nx = Math.max(curPos.getX() - size.getX() + 1, Math.min(delta.getX(), curPos.getX()));
            int ny = Math.max(curPos.getY() - size.getY() + 1, Math.min(delta.getY(), curPos.getY()));
            scrollTo(nx, ny);
        }
        updateIndicator();
    }

    /**
     * Toggles between insert and overwrite modes, updating the {@link #overwrite} flag and
     * the {@code sfCursorIns} state flag that controls the cursor shape.
     */
    protected void toggleInsMode() {
        overwrite = !overwrite;
        setState(sfCursorIns, !hasState(sfCursorIns));
    }

    /**
     * Enters selection mode: sets {@link #selecting} to {@code true} and anchors the
     * selection start/end at the current cursor position.
     */
    protected void startSelect() {
        selecting = true;
        selStart = curPtr;
        selEnd = curPtr;
    }

    /**
     * Exits selection mode and collapses the selection to the cursor position.
     */
    protected void hideSelect() {
        selecting = false;
        selStart = selEnd = curPtr;
    }

    /**
     * Copies the selected text to the clipboard.
     *
     * @return {@code true} if text was copied; {@code false} if there was no selection
     */
    protected boolean clipCopy() {
        if (!hasSelection()) {
            return false;
        }
        int a = Math.min(selStart, selEnd);
        int b = Math.max(selStart, selEnd);
        JtvProgram.getClipboard().setContents(new StringSelection(buffer.substring(a, b)), null);
        return true;
    }

    /**
     * Copies the selected text to the clipboard then deletes it from the buffer.
     * Does nothing if there is no selection.
     */
    protected void clipCut() {
        if (clipCopy()) {
            deleteSelect();
        }
    }

    /**
     * Inserts the clipboard text at the cursor position, replacing any current selection.
     * Does nothing if the clipboard holds no text.
     */
    protected void clipPaste() {
        try {
            Transferable t = JtvProgram.getClipboard().getContents(null);
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                insertText((String) t.getTransferData(DataFlavor.stringFlavor), false);
            }
        }
        catch (Exception e) {
            // ignore
        }
    }

    /**
     * Reverses the last recorded edit using the stored delta, then swaps in the inverse
     * delta so the operation can be redone by calling {@code undo()} again.
     * Does nothing if no snapshot is available.
     */
    protected void undo() {
        if (!canUndoState) {
            return;
        }
        // Capture params needed for incremental metrics update before we overwrite them.
        int metricsOffset   = undoOffset;
        int metricsDeleted  = undoInsertedLength;        // chars to delete from current buffer
        int metricsInserted = undoDeletedText.length();  // chars to insert

        // Compute the inverse delta (for redo) before modifying the buffer.
        int redoOffset = undoOffset;
        int end = Math.min(undoOffset + undoInsertedLength, buffer.length());
        String redoDeleted = undoInsertedLength > 0 ? buffer.substring(undoOffset, end) : "";
        int redoInserted = undoDeletedText.length();
        int redoCurPtr = curPtr;
        int redoSelStart = selStart;
        int redoSelEnd = selEnd;

        // Apply the undo delta: reverse of (delete undoDeletedText, insert undoInsertedLength chars).
        if (undoInsertedLength > 0) {
            buffer.delete(undoOffset, Math.min(undoOffset + undoInsertedLength, buffer.length()));
        }
        if (!undoDeletedText.isEmpty()) {
            buffer.insert(undoOffset, undoDeletedText);
        }

        curPtr = Math.max(0, Math.min(undoCurPtr, buffer.length()));
        selStart = Math.max(0, Math.min(undoSelStart, buffer.length()));
        selEnd = Math.max(0, Math.min(undoSelEnd, buffer.length()));

        // Store inverse delta so the next undo() call redoes this operation.
        undoOffset = redoOffset;
        undoDeletedText = redoDeleted;
        undoInsertedLength = redoInserted;
        undoCurPtr = redoCurPtr;
        undoSelStart = redoSelStart;
        undoSelEnd = redoSelEnd;
        canUndoState = true;

        modified = true;
        notifyTitleUpdate();
        canUndo = true;
        updateMetricsIncremental(metricsOffset, metricsDeleted, metricsInserted);
        trackCursor(false);
    }

    /**
     * Opens the Find dialog.  The base implementation is a no-op; override in subclasses
     * (e.g. {@link JtvFileEditor}) to present a real dialog.
     */
    protected void doFind() {
    }

    /**
     * Opens the Replace dialog.  The base implementation is a no-op; override in subclasses
     * to present a real dialog.
     */
    protected void doReplace() {
    }

    /**
     * Repeats the most recent search/replace operation.  The base implementation is a no-op;
     * override in subclasses to implement the actual repeat.
     */
    protected void doSearchAgain() {
    }

    /**
     * Enables or disables the standard edit commands ({@code cmUndo}, {@code cmCut},
     * {@code cmCopy}, {@code cmPaste}, {@code cmClear}, {@code cmFind}, {@code cmReplace},
     * {@code cmSearchAgain}) based on the current selection and clipboard state.
     * Called by {@link #updateMetrics()} and {@link #setState}.
     */
    protected void updateCommands() {
        boolean hasSel = hasSelection();
        setCmdState(cmUndo, !readOnly && canUndo && canUndoState);
        setCmdState(cmCut, !readOnly && hasSel);
        setCmdState(cmCopy, hasSel);
        Transferable t = JtvProgram.getClipboard().getContents(null);
        setCmdState(cmPaste, !readOnly && t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor));
        setCmdState(cmClear, !readOnly && hasSel);
        setCmdState(cmFind, true);
        setCmdState(cmReplace, !readOnly);
        setCmdState(cmSearchAgain, true);
    }

    /**
     * Enables or disables a single command depending on {@code enable}.
     *
     * @param command the command code to update
     * @param enable  {@code true} to enable, {@code false} to disable
     */
    protected void setCmdState(int command, boolean enable) {
        if (enable) {
            enableCommand(command);
        }
        else {
            disableCommand(command);
        }
    }

    /**
     * Propagates the {@code sfActive} state change to the associated scroll bars and indicator,
     * showing or hiding them as the editor gains or loses focus, then refreshes the command set.
     *
     * @param aState state flag(s) being changed
     * @param enable {@code true} to set the flag, {@code false} to clear it
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & sfActive) != 0) {
            if (hScrollBar != null) {
                hScrollBar.setState(org.viktor44.jtvision.core.ViewFlags.sfVisible, enable);
            }
            if (vScrollBar != null) {
                vScrollBar.setState(org.viktor44.jtvision.core.ViewFlags.sfVisible, enable);
            }
            if (indicator != null) {
                indicator.setState(org.viktor44.jtvision.core.ViewFlags.sfVisible, enable);
            }
            updateCommands();
        }
    }

    /**
     * Handles mouse, keyboard, command, and scroll-bar broadcast events.
     *
     * <p>Mouse clicks move the cursor; mouse drag extends the selection.
     * Printable key presses insert (or overwrite) characters.
     * Navigation keys ({@code kbLeft}, {@code kbRight}, …) move the cursor,
     * optionally extending the selection when {@link #selecting} is active.
     * Command events are dispatched to the appropriate editing operations.
     * Scroll-bar broadcasts update {@link #delta} and redraw.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        int selectMode = selecting ? 1 : 0;
        if (event.getWhat() == evMouseDown) {
            do {
                JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                if (mouse.getX() < 0) {
                    delta = new JtvPoint(Math.max(0, delta.getX() - 1), delta.getY());
                }
                else if (mouse.getX() >= size.getX()) {
                    delta = new JtvPoint(Math.min(Math.max(0, limit.getX() - size.getX()), delta.getX() + 1), delta.getY());
                }
                if (mouse.getY() < 0) {
                    delta = new JtvPoint(delta.getX(), Math.max(0, delta.getY() - 1));
                }
                else if (mouse.getY() >= size.getY()) {
                    delta = new JtvPoint(delta.getX(), Math.min(Math.max(0, limit.getY() - size.getY()), delta.getY() + 1));
                }
                mouse = new JtvPoint(
                		Math.max(0, Math.min(mouse.getX(), Math.max(0, size.getX() - 1))),
                        Math.max(0, Math.min(mouse.getY(), Math.max(0, size.getY() - 1)))
                );
                int p = lineToPtr(mouse.getY() + delta.getY(), mouse.getX() + delta.getX());
                setCurPtr(p, selectMode);
                selectMode |= 1;
                trackCursor(false);
                drawView();
            }
            while (mouseEvent(event, evMouseMove | evMouseAuto));
            clearEvent(event);
            return;
        }

        if (event.getWhat() == evKeyDown) {
            int stroke = event.getKeyDown().getKeyStroke();
            if ((event.getKeyDown().getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                selectMode = 1;
            }
            char keyChar = event.getKeyDown().getKeyChar();
            if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar >= 32) {
                if (!readOnly) {
                    if (overwrite && !hasSelection() && curPtr < buffer.length() && buffer.charAt(curPtr) != '\n') {
                        deleteRange(curPtr, curPtr + 1);
                    }
                    insertText(String.valueOf(keyChar), false);
                    clearEvent(event);
                    drawView();
                }
                return;
            }

            // Compound shortcuts first; navigation keys ignore the Shift modifier (used for selection).
            if (stroke == ((InputEvent.SHIFT_DOWN_MASK << 16) | KeyEvent.VK_DELETE)) {
                if (!readOnly) {
                	clipCut();
                }
            }
            else if (stroke == ((InputEvent.CTRL_DOWN_MASK << 16) | KeyEvent.VK_INSERT)) {
                clipCopy();
            }
            else if (stroke == ((InputEvent.SHIFT_DOWN_MASK << 16) | KeyEvent.VK_INSERT)) {
                if (!readOnly) {
                	clipPaste();
                }
            }
            else {
                switch (stroke) {
                    case KeyEvent.VK_LEFT: 
                    	setCurPtr(prevChar(curPtr), selectMode);
                    	break;
                    case KeyEvent.VK_RIGHT:
                    	setCurPtr(nextChar(curPtr), selectMode);
                    	break;
                    case KeyEvent.VK_UP:
                    	setCurPtr(prevLine(curPtr), selectMode);
                    	break;
                    case KeyEvent.VK_DOWN:
                    	setCurPtr(nextLine(curPtr), selectMode);
                    	break;
                    case KeyEvent.VK_HOME:
                    	setCurPtr(lineStart(curPtr), selectMode);
                    	break;
                    case KeyEvent.VK_END:
                    	setCurPtr(lineEnd(curPtr), selectMode);
                    	break;
                    case KeyEvent.VK_PAGE_UP:
                    	setCurPtr(Math.max(0, curPtr - (size.getX() * size.getY())), selectMode);
                    	break;
                    case KeyEvent.VK_PAGE_DOWN:
                    	setCurPtr(Math.min(bufLen, curPtr + (size.getX() * size.getY())), selectMode);
                    	break;
                    case KeyEvent.VK_BACK_SPACE:
                    	if (!readOnly) {
                    		deleteRange(prevChar(curPtr), curPtr); 
                    	}
                    	break;
                    case KeyEvent.VK_DELETE: 
                    	if (!readOnly) {
                    		deleteRange(curPtr, nextChar(curPtr)); 
                    	}
                    	break;
                    case KeyEvent.VK_ENTER:
                    	if (!readOnly) {
                    		newLine();
                    	}
                    	break;
                    case KeyEvent.VK_INSERT:
                    	if (!readOnly) {
                    		toggleInsMode();
                    	}
                    	break;
                    default: 
                    	return;
                }
            }

            if ((event.getKeyDown().getModifiers() & InputEvent.SHIFT_DOWN_MASK) == 0) {
                selecting = false;
                selStart = selEnd = curPtr;
            }
            trackCursor(false);
            clearEvent(event);
            drawView();
            return;
        }

        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmFind: 
                	doFind(); 
                	break;
                case cmReplace:
                	doReplace();
                	break;
                case cmSearchAgain:
                	doSearchAgain();
                	break;
                case cmCut:
                	if (!readOnly) clipCut();
                	break;
                case cmCopy:
                	clipCopy();
                	break;
                case cmPaste:
                	if (!readOnly) clipPaste();
                	break;
                case cmUndo:
                	if (!readOnly) undo();
                	break;
                case cmClear:
                	if (!readOnly) deleteSelect();
                	break;
                case cmCharLeft:
                	setCurPtr(prevChar(curPtr), selectMode);
                	break;
                case cmCharRight:
                	setCurPtr(nextChar(curPtr), selectMode);
                	break;
                case cmWordLeft:
                	setCurPtr(prevWord(curPtr), selectMode);
                	break;
                case cmWordRight:
                	setCurPtr(nextWord(curPtr), selectMode);
                	break;
                case cmLineStart:
                	setCurPtr(lineStart(curPtr), selectMode);
                	break;
                case cmLineEnd:
                	setCurPtr(lineEnd(curPtr), selectMode);
                	break;
                case cmLineUp:
                	setCurPtr(prevLine(curPtr), selectMode);
                	break;
                case cmLineDown:
                	setCurPtr(nextLine(curPtr), selectMode);
                	break;
                case cmPageUp:
                	setCurPtr(Math.max(0, curPtr - (size.getX() * size.getY())), selectMode);
                	break;
                case cmPageDown:
                	setCurPtr(Math.min(bufLen, curPtr + (size.getX() * size.getY())), selectMode);
                	break;
                case cmTextStart:
                	setCurPtr(0, selectMode);
                	break;
                case cmTextEnd:
                	setCurPtr(bufLen, selectMode);
                	break;
                case cmNewLine:
                	if (!readOnly) newLine();
                	break;
                case cmBackSpace:
                	if (!readOnly) deleteRange(prevChar(curPtr), curPtr);
                	break;
                case cmDelChar:
                	if (!readOnly) deleteRange(curPtr, nextChar(curPtr));
                	break;
                case cmDelWord:
                	if (!readOnly) deleteRange(curPtr, nextWord(curPtr));
                	break;
                case cmDelWordLeft:
                	if (!readOnly) deleteRange(prevWord(curPtr), curPtr);
                	break;
                case cmDelStart:
                	if (!readOnly) deleteRange(lineStart(curPtr), curPtr);
                	break;
                case cmDelEnd:
                	if (!readOnly) deleteRange(curPtr, lineEnd(curPtr));
                	break;
                case cmDelLine:
                	if (!readOnly) deleteRange(lineStart(curPtr), nextLine(curPtr));
                	break;
                case cmInsMode:
                	if (!readOnly) toggleInsMode();
                	break;
                case cmStartSelect:
                	startSelect();
                	break;
                case cmHideSelect:
                	hideSelect();
                	break;
                case cmIndentMode:
                	autoIndent = !autoIndent;
                	break;
                case cmSelectAll:
                	selStart = 0; selEnd = bufLen; curPtr = bufLen;
                	break;
                case cmEncoding:
                	break;
                default:
                	return;
            }
            updateMetrics();
            trackCursor(false);
            clearEvent(event);
            drawView();
            return;
        }

        if (event.getWhat() == evBroadcast) {
            if (event.getMessage().getCommand() == org.viktor44.jtvision.core.CommandCodes.cmScrollBarChanged) {
                if (event.getMessage().getInfoPtr() == hScrollBar) {
                    delta = new JtvPoint(hScrollBar.getValue(), delta.getY());
                    drawView();
                }
                else if (event.getMessage().getInfoPtr() == vScrollBar) {
                    delta = new JtvPoint(delta.getX(), vScrollBar.getValue());
                    drawView();
                }
            }
        }
    }

    /**
     * Returns {@code true} if this editor view is in a valid state.
     * The base implementation always returns {@link #isValid}.
     *
     * @param command the validation command (e.g. {@code cmValid}, {@code cmClose})
     * @return {@code true} if the editor is valid and may be used/closed
     */
    @Override
    public boolean valid(int command) {
        return isValid;
    }
}

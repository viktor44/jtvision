package org.viktor44.jtvision.examples.tvdemo;

import static org.viktor44.jtvision.core.ViewFlags.wfGrow;
import static org.viktor44.jtvision.core.ViewFlags.wfZoom;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvWindow;

public class PuzzleWindow extends JtvWindow {

    public PuzzleWindow() {
        super(new JtvRect(1, 1, 21, 7), "Puzzle", wnNoNumber);
        flags &= ~(wfZoom | wfGrow);
        growMode = 0;
        JtvRect r = getExtent();
        r.grow(-1, -1);
        insert(new PuzzleView(r));
    }
}

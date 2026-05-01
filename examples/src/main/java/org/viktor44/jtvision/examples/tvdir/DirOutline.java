package org.viktor44.jtvision.examples.tvdir;

import static org.viktor44.jtvision.core.CommandCodes.cmOutlineItemSelected;
import static org.viktor44.jtvision.core.EventCodes.evCommand;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvNode;
import org.viktor44.jtvision.views.JtvOutline;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * Directory tree outline based on core TOutline.
 */
public class DirOutline extends JtvOutline {

    public static final int cmNewDirFocused = 10200;

    public DirOutline(JtvRect bounds, JtvScrollBar hsb, JtvScrollBar vsb, JtvNode root) {
        super(bounds, hsb, vsb, root);
    }

    @Override
    public void focused(int i) {
        super.focused(i);
        notifyNewDir();
    }

    @Override
    public void selected(int i) {
        super.selected(i);
        notifyNewDir();
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand && event.getMessage().getCommand() == cmOutlineItemSelected) {
            notifyNewDir();
            clearEvent(event);
        }
    }

    private void notifyNewDir() {
        JtvEvent e = new JtvEvent();
        e.setWhat(evCommand);
        e.getMessage().setCommand(cmNewDirFocused);
        e.getMessage().setInfoPtr(this);
        if (owner != null) {
            owner.handleEvent(e);
        }
    }

    public String getCurrentPath() {
        JtvNode node = getNode(foc);
        if (node instanceof DirNode) {
            DirNode dirNode = (DirNode) node;
            if (dirNode.file != null) {
                return dirNode.file.getAbsolutePath();
            }
        }

        JtvNode r = getRoot();
        if (r instanceof DirNode) {
            DirNode rootDir = (DirNode) r;
            if (rootDir.file != null) {
                return rootDir.file.getAbsolutePath();
            }
        }
        return ".";
    }
}

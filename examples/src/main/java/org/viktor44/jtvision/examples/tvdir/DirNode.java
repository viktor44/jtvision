package org.viktor44.jtvision.examples.tvdir;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.viktor44.jtvision.views.JtvNode;

/**
 * Directory-outline node based on core TNode.
 */
public class DirNode extends JtvNode {

    public final File file;

    public DirNode(File file, JtvNode childList, JtvNode next, boolean expanded) {
        super(file != null ? file.getName() : "", childList, next, expanded);
        this.file = file;
        if (file != null) {
            if (getText() == null || getText().isEmpty()) {
                setText(file.getAbsolutePath());
            }
        }
    }

    public static DirNode buildTree(File root) {
        return buildTree(root, true);
    }

    private static DirNode buildTree(File file, boolean expanded) {
        DirNode node = new DirNode(file, null, null, expanded);
        node.setChildList(buildChildren(file));
        return node;
    }

    private static JtvNode buildChildren(File parent) {
        File[] kids = parent != null ? parent.listFiles(File::isDirectory) : null;
        if (kids == null || kids.length == 0) {
            return null;
        }
        Arrays.sort(kids, Comparator.comparing(f -> f.getName().toLowerCase()));
        JtvNode head = null;
        JtvNode tail = null;
        for (File kid : kids) {
            DirNode child = buildTree(kid, false);
            if (head == null) {
                head = child;
                tail = child;
            } else {
                tail.setNext(child);
                tail = child;
            }
        }
        return head;
    }
}

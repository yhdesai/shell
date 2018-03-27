package me.isaiah.shell;

import javax.swing.JInternalFrame;

public class JProgram extends JInternalFrame {
    private static final long serialVersionUID = 1L;

    public JProgram(String title) {
        this(title, true, true, true);
    }

    public JProgram(String title, boolean a, boolean b, boolean c) {
        super(title, a, b, c);
        this.toFront();
    }
}

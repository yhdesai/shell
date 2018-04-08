package me.isaiah.shell.api;

import javax.swing.JInternalFrame;

/**
 * API class for Program developers
 * 
 * In most cases you can replace JFrames with this
 * as this is a JInternalFrame
 */
public class JProgram extends JInternalFrame {
    private static final long serialVersionUID = 1L;

    /**
     * Main contructor for programs
     * The window will be closable, resizable, & maximizable.
     */
    public JProgram(String title) {
        this(title, true, true, true);
    }

    public JProgram(String title, boolean resizable, boolean closable, boolean maximizable) {
        super(title, resizable, closable, maximizable);
        this.toFront();
    }

    /**
     * Registers this program to the program menu bar.
     * TODO: This method has no functionality yet.
     */
    public void register() {
    }
}

package me.isaiah.shell;

import me.isaiah.shell.api.JProgram;

/**
 * App "store" (free only programs) for JShell.
 * 
 * TODO:
 *  Create program class loader & port
 *      {@link me.isaiah.shell.Calc}<br>
 *      {@link me.isaiah.shell.MineSweeper}<br>
 *      {@link me.isaiah.shell.MiniBrowser}<br>
 */
public class ProgramListing extends JProgram {
    private static final long serialVersionUID = 1L;

    public ProgramListing(String title) {
        super(title);
    }
}
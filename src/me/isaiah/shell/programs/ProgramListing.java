package me.isaiah.shell.programs;

import me.isaiah.shell.api.JProgram;

/**
 * App "store" (free only programs) for JShell.
 * 
 * TODO:
 *  Create program class loader & port
 *      {@link me.isaiah.shell.programs.Calc}<br>
 *      {@link me.isaiah.shell.programs.MineSweeper}<br>
 *      {@link me.isaiah.shell.programs.MiniBrowser}<br>
 */
public class ProgramListing extends JProgram {
    private static final long serialVersionUID = 1L;

    public ProgramListing(String title) {
        super(title);
    }
}
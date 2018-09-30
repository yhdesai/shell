package me.isaiah.shell.legacy;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;

/**
 * @deprecated Legacy Icon system, Replaced by {@link DesktopIcon}.
 * To be removed in the next major release.
 */
@Deprecated
public class LEGACY_Icon extends JButton {

    private static final long serialVersionUID = 1L;

    public LEGACY_Icon(String name) {
        this(name, false);
    }
    
    public LEGACY_Icon(String name, boolean folder) {
        super(name);
        if (folder) this.setBackground(Color.ORANGE);
        else this.setBackground(Color.CYAN);

        if (name.endsWith(".txt") || name.endsWith(".text") || name.endsWith(".html")) this.setBackground(Color.GREEN);
        if (name.endsWith(".jar")) this.setBackground(Color.YELLOW);

        if (name.length() > 14) this.setFont(new Font("Arial", Font.PLAIN, 7));
        else this.setFont(new Font("Arial", Font.PLAIN, 10));
    }

}
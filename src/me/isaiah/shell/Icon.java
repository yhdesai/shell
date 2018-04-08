package me.isaiah.shell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

public class Icon extends JButton {
    private static final long serialVersionUID = 1L;

    public Icon(String name) {
        this(name, false);
    }
    
    public Icon(String name, boolean folder) {
        super(name);
        if (folder) this.setBackground(Color.ORANGE);
        else this.setBackground(Color.CYAN);
        
        if (name.endsWith(".txt") || name.endsWith(".text") || name.endsWith(".html")) this.setBackground(Color.GREEN);
        if (name.endsWith(".jar")) this.setBackground(Color.YELLOW);
        
        if (name.length() > 14) this.setFont(new Font("Arial", Font.PLAIN, 7));
        else this.setFont(new Font("Arial", Font.PLAIN, 10));
 
        this.setMaximumSize(new Dimension(1000000, 10));
    }
}

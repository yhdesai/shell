package me.isaiah.shell;

import java.awt.Font;

import javax.swing.JButton;

public class Button extends JButton {
    private static final long serialVersionUID = 1L;

    public Button(String name) {
        super(name);
        this.setFont(new Font("Arial", Font.PLAIN, 10));
    }
}

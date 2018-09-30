package me.isaiah.shell;

import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import me.isaiah.shell.api.JProgram;

public class Notification extends JProgram {
    private static final long serialVersionUID = 1L;
    private JTextArea cont;

    public Notification(String content, int ms) {
        super("Notification", false, true, false);
        this.setVisible(true);
        this.cont = new JTextArea(content);
        this.cont.setMargin(new Insets(10, 10, 10, 10));
        this.cont.setEditable(false);
        this.setContentPane(cont);
        this.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        this.setLocation(this.getRes().x, this.getRes().y);
        this.validate();
        Timer timer = new Timer(ms, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO anamations
                setVisible(false);
                dispose();
                ((Timer)e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public JTextArea getContent() {
        return cont;
    }

    public Rectangle getRes() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return environment.getMaximumWindowBounds();
    }
}
package me.isaiah.shell;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import me.isaiah.shell.api.JProgram;

public class Console extends JProgram {
    private static final long serialVersionUID = 1L;
    protected static JTextArea area = new JTextArea(24, 80); 
    public Console() {
        super("Console Output");
        area.setBackground(Color.BLACK);
        area.setForeground(Color.LIGHT_GRAY);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane p = new JScrollPane(area);
        add(p);
    }

    public static void init() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override public void write(int b) throws IOException {
              area.append(String.valueOf((char) b));
            }}));
    }
}

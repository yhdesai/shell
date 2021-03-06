package me.isaiah.shell.programs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import me.isaiah.shell.Commands;
import me.isaiah.shell.Main;
import me.isaiah.shell.api.JProgram;
import me.isaiah.shell.api.ProgramInfo;

@ProgramInfo(name = "CMD Prompt")
public class Console extends JProgram {

    private static final long serialVersionUID = 1L;
    private static JTextPane area;

    public Console() {
        super("Console Output");
        area = new JTextPane();
        area.setText("ZunoZap OS [Version " + Main.VERSION + "]\n(C) 2018 ZunoZap Contributors");
        area.setBackground(Color.BLACK);
        area.setForeground(Color.LIGHT_GRAY);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane p = new JScrollPane(area);
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(1000000, 100));
        Commands c = new Commands(area, this);
        f.addActionListener(l -> c.onCommand(f,l));
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        pan.add(p);
        pan.add(f);
        setContentPane(pan);
    }

}
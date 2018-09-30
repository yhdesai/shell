package me.isaiah.shell.programs;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import me.isaiah.shell.Main;
import me.isaiah.shell.MouseClick;
import me.isaiah.shell.api.JProgram;

public class ActiveDesktop extends JProgram {

    private static final long serialVersionUID = 1L;

    public ActiveDesktop() {
        super("This text should be hidden!");
        setLocation(0, 0);
        putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        ((BasicInternalFrameUI)getUI()).setNorthPane(null);

        new Thread(() -> {
            try {
                Image i = ImageIO.read(new URL("https://avatars.mds.yandex.net/get-pdb/33827/369cb281-1bb7-448d-9df1-cbe68da08025/orig"));
                i = i.getScaledInstance(Main.p.getWidth(), Main.p.getHeight(), 0);
                setContentPane(new JLabel(new ImageIcon(i)));
            } catch (IOException e1) { e1.printStackTrace(); }
        }).start();
        setBorder(null);
        setVisible(true);
        pack();
        setSize(Main.p.getWidth(), Main.p.getHeight());
        validate();
        this.addMouseListener(new MouseClick() {
            @Override public void click(MouseEvent e) { moveToBack(); }
        });
        setResizable(false);
        Main.p.add(this);
        Main.p.setComponentZOrder(this, 0);
        Main.p.moveToBack(this);
    }
    
    public void moveToBack() {
        Main.p.setComponentZOrder(this, 0);
        Main.p.moveToBack(this);
    }

}

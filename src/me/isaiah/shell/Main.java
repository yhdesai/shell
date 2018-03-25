package me.isaiah.shell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Main {
    public static final String VERSION = "1.0-dev";
    public static final Runtime r = Runtime.getRuntime();
    protected static JPanel p = new JPanel();

    public static void main(String[] args) {
        if (((r.maxMemory() / 1024) / 1024) < 10) {
            System.err.println("System does not meet the requirements to run jShell.");
            System.err.println("JVM max memory of " + (r.maxMemory() / 1024 / 1024) + " MB does not meet the required 10 MB");
            System.exit(1);
            return;
        }
        System.out.println("Starting jShell version " + VERSION);
        System.out.println(getInfo());

        JFrame f = new JFrame();
        f.setBackground(Color.ORANGE);
        f.setLayout(null);

        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        int len = desktop.listFiles().length;
        JPanel pa = new JPanel(new GridLayout(len > 5 ? len / 5 : 3, 1));
        JInternalFrame in = new JInternalFrame("Desktop");
        for (File file : desktop.listFiles()) {
            Icon i = new Icon(file.getName(), file.isDirectory());
            new DragListener(i).addHandle(i);
            pa.add(i);

            i.addActionListener((l) -> newFileExplorer(file));
        }
        in.setContentPane(pa);
        in.setClosable(true);
        new DragListener(in, MouseEvent.BUTTON1).addHandle(in);
        in.setVisible(true);
        p.add(in);

        p.setBackground(new Color(51, 153, 255));
        
        JMenuBar b = new JMenuBar();
        JMenu e = new JMenu("Exit");
        e.addActionListener((l) -> System.exit(0));
        e.addMouseListener(new MouseClick() {
            @Override public void mouseClicked(MouseEvent e) {System.exit(0);}
        });
        JMenu a = new JMenu("About jShell");
        a.addMouseListener(new MouseClick() {
            @Override public void mouseClicked(MouseEvent e) { showAbout(); }
        });

        JMenu program = new JMenu("Programs");
        JMenuItem wb = program.add("Web Browser");
        wb.addActionListener((l) -> { startBrowser(); });

        JMenuItem np = program.add("NotePad");
        np.addActionListener((l) -> { try {
            emptyNotePad();
        } catch (IOException e1) {
            e1.printStackTrace();
        } });

        JMenuItem fe = program.add("File Explorer");
        fe.addActionListener((l) -> { 
            File dir = desktop;
            while (dir.getParent() != null) {
                dir = dir.getParentFile();
            }
            newFileExplorer(dir); 
        });
        
        JMenuItem lw = program.add("Lite Web Browser");
        lw.addActionListener((l) -> { startLiteBrowser(); });

        program.add("Calcalator").addActionListener((l) -> startCalc());
        program.add("Minesweeper").addActionListener((l) -> minesweeper());

        b.setBounds(-1, (int) (f.getBounds().getCenterY() * 1.9), b.getWidth(), b.getHeight());
        b.add(e);
        b.add(a);
        b.add(program);
        f.setJMenuBar(b);

        f.setContentPane(p);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setUndecorated(true);
        f.setMaximumSize(new Dimension(500,500));
        f.pack();
        f.setVisible(true);
    }

    public static String getInfo() {
        return "Created by Isaiah Patton\n"
                + "Version " + VERSION + "\n"
                + "Installed RAM: " + ((r.maxMemory() / 1024) / 1024) + " MB\n"
                + "\nSoftware used:\n"
                + " - Calculator @ javacodex.com\n"
                + " - MineSweeper @ java2s.com";
    }

    protected static void newFileExplorer(File file) {
        if (file.isDirectory()) {
            int le = file.listFiles().length;
            JPanel pan = new JPanel(new GridLayout(le > 5 ? le / 5 : 3, 1));
            JInternalFrame inf = new JInternalFrame("[File Explorer] " + file.getName());
            inf.toFront();
            for (File fil : file.listFiles()) {
                Icon ic = new Icon(fil.getName(), fil.isDirectory());
                ic.addActionListener((l) -> {
                   newFileExplorer(fil); 
                });
                new DragListener(ic).addHandle(ic);
                pan.add(ic);
            }
            inf.setContentPane(pan);
            inf.setClosable(true);
            new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
            inf.setVisible(true);
            p.add(inf);
        } else {
            String name = file.getName();
            if (name.endsWith(".txt") || name.endsWith(".text") || name.endsWith(".html")) try {
                newNotePad(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected static void emptyNotePad() throws IOException {
        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        newNotePad(new File(desktop, "New-Doc-" + new Random().nextInt(20) + ".txt"));
    }

    protected static void newNotePad(File file) throws IOException {
        file.createNewFile();
        JInternalFrame inf = new JInternalFrame("[NotePad] " + file.getName());
        new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
        String text = "\n";
        if (file != null) for (String s : Files.readAllLines(file.toPath())) text += "\n" + s;

        JPanel pa = new JPanel();
        JTextArea a = new JTextArea(text);
        a.setMargin(new Insets(5, 8, 5, 8));
        JButton s = new JButton("Save");
        a.setWrapStyleWord(true);
        a.setSize(200, 300);
        s.setSize(50, 50);

        s.addActionListener((l) -> {
            try {
                Files.write(file.toPath(), a.getText().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pa.add(a);
        pa.add(s);
        inf.setContentPane(pa);
        inf.setClosable(true);
        inf.setPreferredSize(new Dimension(320,300));
        inf.setVisible(true);
        p.add(inf);
    }

    private static void startBrowser() {
        JInternalFrame inf = new JInternalFrame("ZunoZap for jShell");
        JFXPanel pan = new JFXPanel();
        Platform.runLater(() -> {
            WebView w = new WebView();
            WebEngine e = w.getEngine();
            e.setUserAgent(e.getUserAgent() + " ZunoZap/0.1-jShell jShell/1.0");
            e.load("https://start.duckduckgo.com/");
            pan.setScene(new Scene(w));
        });
        inf.setContentPane(pan);
        inf.setClosable(true);
        new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
        inf.setSize(200, 200);
        inf.setVisible(true);
        p.add(inf);
    }
    
    private static void startLiteBrowser() {
        JInternalFrame inf = new MiniBrowser();
        
        inf.setClosable(true);
        new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
        inf.setSize(200, 500);
        Timer timer = new Timer(1000, new ActionListener() {
            @Override public void actionPerformed(ActionEvent arg0) {
                inf.setSize(inf.getWidth(), 480);
            }
          });
        timer.setRepeats(false);
          timer.start();
        inf.setVisible(true);
        p.add(inf);
    }
    
    private static void startCalc() {
        JInternalFrame inf = new Calc();
        
        inf.setClosable(true);
        new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
        inf.setVisible(true);
        p.add(inf);
    }
    
    private static void minesweeper() {
        JInternalFrame inf = new JInternalFrame();
        inf.setContentPane(new MineSweeper());
        inf.setClosable(true);
        inf.setVisible(true);
        Timer timer = new Timer(500, new ActionListener() {
            @Override public void actionPerformed(ActionEvent arg0) {
                inf.setSize(inf.getWidth(), 350);
            }
          });
        timer.setRepeats(false);
        timer.start();
        new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
        p.add(inf);
    }

    private static void showAbout() {
        JInternalFrame inf = new JInternalFrame("About jShell");
        JPanel pan = new JPanel();
        JTextArea n = new JTextArea(" jShell");
        n.setFont(new Font("Arial", Font.BOLD, 32));
        JTextArea a = new JTextArea(getInfo());
        n.setEditable(false);
        a.setMargin(new Insets(0, 5, 5, 5));
        n.setMargin(new Insets(5, 5, 0, 5));
        a.setEditable(false);
        pan.setLayout(new GridLayout(2, 0));
        pan.add(n);
        pan.add(a);
        inf.setContentPane(pan);
        inf.setClosable(true);
        new DragListener(inf, MouseEvent.BUTTON1).addHandle(inf);
        inf.setVisible(true);
        p.add(inf);
    }
}
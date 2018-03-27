package me.isaiah.shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class Main {
    public static final String VERSION = "0.2";
    public static final Runtime r = Runtime.getRuntime();
    public static int ram = (int) ((r.maxMemory() / 1024) / 1024);
    public static boolean supportsFxBrowser = true;
    protected static JDesktopPane p = new JDesktopPane();

    public static void main(String[] args) {
        if (ram < 20) {
            System.err.println("System does not meet the requirements to run jShell.");
            System.err.println("JVM max memory of " + ram + " MB does not meet the required 20 MB");
            System.exit(1);
            return;
        }
        if (ram < 200) {
            supportsFxBrowser = false;
            System.err.println("System does not meet the requirements to run the web browser");
            System.err.println("JVM max memory of " + ram + " MB does not meet the required 200 MB");
        }
        System.out.println("Running jShell version " + VERSION);

        JFrame f = new JFrame();
        f.setBackground(Color.ORANGE);
        //f.setLayout(null);

        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        //int len = desktop.listFiles().length;
        //JPanel pa = new JPanel(new GridLayout(len > 5 ? len / 5 : 3, 1));
        /*JProgram in = new JProgram("Desktop");
        for (File file : desktop.listFiles()) {
            Icon i = new Icon(file.getName(), file.isDirectory());
            new DragListener(i).addHandle(i);

            pa.add(i);

            i.addActionListener((l) -> newFileExplorer(file));
        }
        in.setSize(pa.getMinimumSize());
        in.setContentPane(pa);
        in.setClosable(true);
        new DragListener(in, MouseEvent.BUTTON1).addHandle(in);
        in.setVisible(true);
        p.add(in);*/
        newFileExplorer(desktop);

        p.setBackground(new Color(51, 153, 255));

        JMenuBar b = new JMenuBar();
        JMenu e = new JMenu("Exit");
        e.addActionListener((l) -> System.exit(0));
        e.addMouseListener(new MouseClick() { @Override public void mouseClicked(MouseEvent e) {System.exit(0);}});
        JMenu a = new JMenu("About jShell");
        a.addMouseListener(new MouseClick() { @Override public void mouseClicked(MouseEvent e) { showAbout(); }});

        JMenu program = new JMenu("Programs");
        JMenuItem wb = program.add("Web Browser");
        wb.addActionListener((l) -> startBrowser());
        wb.setEnabled(supportsFxBrowser);

        JMenuItem fe = program.add("File Explorer");
        fe.addActionListener((l) -> { 
            File dir = desktop;
            while (dir.getParent() != null) dir = dir.getParentFile();

            newFileExplorer(dir); 
        });

        program.add("NotePad").addActionListener((l) -> emptyNotePad());
        program.add("Lite Web Browser").addActionListener((l) -> startLiteBrowser());
        program.add("Calcalator").addActionListener((l) -> startCalc());
        program.add("Minesweeper").addActionListener((l) -> minesweeper());

        b.add(e);
        b.add(a);
        b.add(program);
        f.setJMenuBar(b);
        System.out.println(p.getAllFrames().length);

        //JPanel pae = new JPanel();
        p.setVisible(true);
        //pae.add(p);
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
                + "Installed RAM: " + ram + " MB\n"
                + "Java version: " + System.getProperty("java.version")
                + "\n\nSoftware used:\n"
                + " - Calculator @ javacodex.com\n"
                + " - MineSweeper @ java2s.com"; 
    }

    protected static void newFileExplorer(File file) {
        if (file.isDirectory()) {
            int le = file.listFiles().length;
            JPanel pan = new JPanel(new GridLayout(le > 5 ? le / 5 : 3, 1));
            JProgram inf = new JProgram("[File Explorer] " + file.getName(), true , true, true);
            JPanel pa = new JPanel();

            JTextField field = new JTextField(file.getAbsolutePath());
            pa.add(field, BorderLayout.NORTH);
            for (File fil : file.listFiles()) {
                Icon ic = new Icon(fil.getName(), fil.isDirectory());
                ic.addActionListener((l) -> newFileExplorer(fil));
                new DragListener(ic).addHandle(ic);
                pan.add(ic);
            }
            field.setSize(field.getWidth(), 100);
            field.setMaximumSize(new Dimension(100000, 100));
            field.addKeyListener(new KeyListener() {
                @Override public void keyTyped(KeyEvent e) {}
                @Override public void keyPressed(KeyEvent e) {}
                @Override public void keyReleased(KeyEvent e) {
                    File z = new File(field.getText());
                    if (z.exists()) {
                        if (z.isDirectory()) {
                            pan.removeAll();
                            pan.validate();
                            pa.validate();
                            int le2 = z.listFiles().length;
                            pan.setLayout(new GridLayout(le2 > 5 ? le2 / 5 : 3, 1));
                            inf.setSize(pa.getSize());
                            for (File fi : z.listFiles()) {
                                Icon ic = new Icon(fi.getName(), fi.isDirectory());
                                ic.addActionListener((l) -> newFileExplorer(fi));
                                new DragListener(ic).addHandle(ic);
                                pan.add(ic);
                            }
                            inf.pack();
                            pan.validate();
                            pa.validate();
                        } else newFileExplorer(z);
                    }
                }
            });
            pa.add(pan);
            pa.setLayout(new BoxLayout(pa, BoxLayout.Y_AXIS));
            inf.setSize(pa.getSize());
            inf.setSize(650, 450);

            inf.setContentPane(pa);
            inf.setClosable(true);
            inf.setVisible(true);
            p.add(inf);
        } else {
            String name = file.getName();
            if (name.endsWith(".txt") || name.endsWith(".text") || name.endsWith(".html")) try {
                newNotePad(file);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    protected static void emptyNotePad() {
        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        try {
            newNotePad(new File(desktop, "New-Doc-" + new Random().nextInt(20) + ".txt"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    protected static void newNotePad(File file) throws IOException {
        file.createNewFile();
        JProgram inf = new JProgram("[NotePad] " + file.getName());
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
        inf.setSize(new Dimension(320,300));
        inf.setVisible(true);
        p.add(inf);
    }

    private static void startBrowser() {
        new JFXPanel();
        Platform.runLater(() -> {
            try { Browser.main(null); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private static void startLiteBrowser() {
        JProgram inf = new MiniBrowser();

        inf.setClosable(true);
        inf.setSize(200, 500);
        Timer timer = new Timer(1000, new ActionListener() {
            @Override public void actionPerformed(ActionEvent arg0) { inf.setSize(inf.getWidth(), 480); }
          });
        timer.setRepeats(false);
          timer.start();
        inf.setVisible(true);
        p.add(inf);
    }

    private static void startCalc() {
        JProgram inf = new Calc();
        inf.setClosable(true);
        inf.setVisible(true);
        inf.setSize(200,200);
        p.add(inf);
    }

    private static void minesweeper() {
        JProgram inf = new JProgram("MineSweeper");
        inf.setContentPane(new MineSweeper());
        inf.setClosable(true);
        inf.setVisible(true);
        Timer timer = new Timer(500, new ActionListener() {
            @Override public void actionPerformed(ActionEvent a) { inf.setSize(inf.getWidth(), 350); }
          });
        timer.setRepeats(false);
        timer.start();
        inf.setSize(250, 350);
        p.add(inf);
    }

    private static void showAbout() {
        JProgram inf = new JProgram("About jShell");
        JPanel pan = new JPanel();
        JTextArea n = new JTextArea(" jShell 1");
        n.setFont(new Font("Arial", Font.BOLD, 32));
        JTextArea a = new JTextArea(getInfo());
        n.setEditable(false);
        a.setMargin(new Insets(0, 5, 5, 5));
        n.setMargin(new Insets(5, 5, 0, 5));
        a.setEditable(false);
        pan.setLayout(new GridLayout(2, 0));
        pan.add(n);
        pan.add(a);
        inf.setSize(300, 300);
        inf.setContentPane(pan);
        inf.setClosable(true);
        inf.setVisible(true);
        p.add(inf);
    }
}
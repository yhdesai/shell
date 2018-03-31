package me.isaiah.shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import net.sf.jcarrierpigeon.Notification;
import net.sf.jcarrierpigeon.NotificationQueue;
import net.sf.jcarrierpigeon.WindowPosition;

public class Main {
    public static final String VERSION = "0.3";
    public static final Runtime r = Runtime.getRuntime();
    public static final int ram = (int) ((r.maxMemory() / 1024) / 1024);
    private static String mem;
    public static boolean supportsFxBrowser = true;
    protected static final JDesktopPane p = new JDesktopPane();

    public static void main(String[] args) {
        if (ram < 20) System.err.println("[WARN] JVM memory (" + ram + " MB) is not > 20 MB for good proformance!");

        if (ram < 200) {
            supportsFxBrowser = false;
            System.err.println("JVM max memory of " + ram + " MB does not meet the required 200 MB for web browsing!");
            System.out.println("Disabling ZunoZap Browser.");
        }
        double m = ram;
        if (m > 1024) {
            m = m / 1024;
            String ms = String.valueOf(m);
            if (ms.split("[.]")[1].length() > 3) mem = Double.valueOf(ms.substring(0, ms.indexOf(".") + 2)) + " GB";
            else mem = m + " GB";
        } else mem = m + " MB";

        System.out.println("Running jShell version " + VERSION);

        JFrame f = new JFrame();
        f.setBackground(Color.ORANGE);

        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        newFileExplorer(desktop);

        p.setBackground(new Color(51, 153, 255));

        JMenuBar b = new JMenuBar();
        b.add(new JMenu("Exit")).addMouseListener(new MouseClick() { @Override public void mouseClicked(MouseEvent e) {System.exit(0);}});
        b.add(new JMenu("About jShell")).addMouseListener(new MouseClick(){ @Override public void mouseClicked(MouseEvent e) {about();}});

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
        program.add("Lite Web Browser").addActionListener((l) -> start(new MiniBrowser(), 300, 500));
        program.add("Calcalator").addActionListener((l) -> start(new Calc(), 200, 200));
        program.add("Minesweeper").addActionListener((l) -> start(new MineSweeper(), 250, 350));

        b.add(program);
        f.setJMenuBar(b);

        p.setVisible(true);
        f.setContentPane(p);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setUndecorated(true);
        f.setMaximumSize(new Dimension(500,500));
        f.pack();
        f.setVisible(true);

        String l = getUrlSource("https://api.github.com/repos/isaiahpatton/shell/releases/latest");
        if (!l.equalsIgnoreCase("internet")) {
            l = l.substring(l.indexOf("\"tag_name\":\"") + 12);
            l = l.substring(0, l.indexOf("\","));
        } else showNotification("Could not connect to api.github.com\nto get update infomation.", new Font("Arial", Font.PLAIN, 13),
                5000, 320, 60);

        if (!l.equalsIgnoreCase("internet") && !l.equalsIgnoreCase(VERSION)) {
            JFrame n = new JFrame();
            n.setSize(420,110);
            JTextArea text = new JTextArea("A new jShell Update is ready to be downloaded!"
                    + "\n   Current version: " + VERSION + "\n   Latest version: " + l + "\nhttp://isaiahpatton.github.io/jshell/");
            text.setFont(new Font("Arial", Font.BOLD, 14));
            text.setMargin(new Insets(10, 10, 10, 10));
            text.setEditable(false);
            n.add(text);
            n.setUndecorated(true);
            n.setVisible(true);
            Notification note = new Notification(n, WindowPosition.BOTTOMRIGHT, 0, 0, 15000);
            NotificationQueue queue = new NotificationQueue();
            queue.add(note);
        }
    }

    public static String getUrlSource(String url) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8"))) {
            String line;
            StringBuilder a = new StringBuilder();
            while ((line = in.readLine()) != null) a.append(line);

            return a.toString();
        } catch (IOException e) { return "internet"; }
    }

    public static void showNotification(String tex, int ms) {
        showNotification(tex, new Font("Arial", Font.PLAIN, 13), ms, 420, 110);
    }
    
    public static void showNotification(String tex, Font f, int ms, int width, int height) {
        JFrame n = new JFrame();
        n.setSize(width,height);
        JTextArea text = new JTextArea(tex);
        text.setFont(f);
        text.setMargin(new Insets(10, 10, 10, 10));
        text.setEditable(false);
        n.add(text);
        n.setUndecorated(true);
        n.setVisible(true);
        Notification note = new Notification(n, WindowPosition.BOTTOMRIGHT, 0, 0, 15000);
        NotificationQueue queue = new NotificationQueue();
        queue.add(note);
    }

    public static String getInfo() {
        return "Version " + VERSION + "\n"
                + "Installed RAM: " + mem + "\nJava version: " + System.getProperty("java.version")
                + "\n\nMade possible by:\n"
                + " - Calculator @ javacodex.com\n - MineSweeper @ java2s.com\n"
                + " - JCarrierPigeon @ carrierpigeon.sourceforge.net";
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
                    }}});
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
            if (name.endsWith(".txt") || name.endsWith(".text") || name.endsWith(".html"))
                try { newNotePad(file); } catch (IOException e) { e.printStackTrace(); showNotification(e.getMessage(), 5000); }
        }
    }

    protected static void emptyNotePad() {
        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        try {
            newNotePad(new File(desktop, "New-Doc-" + new Random().nextInt(20) + ".txt"));
        } catch (IOException e) { e.printStackTrace(); showNotification(e.getMessage(), 5000); }
    }

    protected static void newNotePad(File file) throws IOException {
        file.createNewFile();
        JProgram inf = new JProgram("[NotePad] " + file.getName());
        String text = "\n";
        if (file != null) for (String s : Files.readAllLines(file.toPath())) text += "\n" + s;

        JPanel pa = new JPanel();
        JTextArea a = new JTextArea(text);
        a.setMargin(new Insets(5, 8, 5, 8));
        pa.setLayout(new BoxLayout(pa, BoxLayout.Y_AXIS));
        a.setWrapStyleWord(true);
        a.setSize(200, 300);
        JMenuBar m = new JMenuBar();
        JMenu mf = new JMenu("File");
        mf.add("Save").addActionListener((l) -> {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
                for (String line : a.getText().split("\n")) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) { showNotification(e.getMessage(), 5000); e.printStackTrace(); }
        });
        JScrollPane sc = new JScrollPane();
        sc.setViewportView(a);

        pa.add(sc);
        m.add(mf);
        inf.setContentPane(pa);
        inf.setClosable(true);
        inf.setJMenuBar(m);
        inf.setSize(new Dimension(520,500));
        inf.setVisible(true);
        p.add(inf);
    }

    private static void startBrowser() {
        new JFXPanel();
        Platform.runLater(() -> { try { Browser.main(null); } catch (Exception e) { showNotification(e.getMessage(), 5000);
            e.printStackTrace(); }});
    }

    @SuppressWarnings("unused")
    private static final void programManager() {
        JProgram inf = new ProgramListing("Program Manager");
        start(inf, 250, 350);
    }

    private static final void about() {
        JProgram inf = new JProgram("About jShell");
        JPanel pan = new JPanel();
        JTextArea n = new JTextArea(" jShell");
        n.setFont(new Font("Arial", Font.BOLD, 29));
        JTextArea a = new JTextArea(getInfo());
        n.setEditable(false);
        a.setMargin(new Insets(0, 5, 5, 5));
        n.setMargin(new Insets(5, 5, 0, 5));
        a.setEditable(false);
        n.setMaximumSize(new Dimension(50000, 250));
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        pan.add(n);
        pan.add(a);
        inf.setSize(345, 300);
        inf.setContentPane(pan);
        inf.setVisible(true);
        p.add(inf);
    }

    private static final void start(JProgram j, int width, int height) {
        j.setVisible(true);
        j.setSize(width, height);
        p.add(j);
    }
}
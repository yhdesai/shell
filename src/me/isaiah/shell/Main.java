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
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import me.isaiah.shell.api.JProgram;
import net.sf.jcarrierpigeon.Notification;
import net.sf.jcarrierpigeon.NotificationQueue;
import net.sf.jcarrierpigeon.WindowPosition;

public class Main {
    public static final String NAME = "jShell";
    public static final String VERSION = "0.4";
    public static final Runtime r = Runtime.getRuntime();
    public static final int ram = (int) ((r.maxMemory() / 1024) / 1024);
    private static String mem;
    protected static final JDesktopPane p = new JDesktopPane();
    protected static final JMenu programs = new JMenu("Programs");
    private static JProgramManager pm;
    protected static File pStorage = new File(new File(new File(System.getProperty("user.home")),"shell"), "programs.dat");
    protected static ArrayList<String> pr = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;
        @Override public boolean add(String z) {
            boolean b = super.add(z);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(pStorage);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(pr);
                oos.close();
            } catch (IOException e) { e.printStackTrace(); }
            return b;
        }
    };

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Console.init();
        if (ram < 20) System.err.println("[WARN] JVM memory (" + ram + " MB) is not > 20 MB for good proformance!");

        if (ram < 200) {
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

        System.out.println("Running version " + VERSION);

        JFrame f = new JFrame();
        f.setBackground(Color.ORANGE);

        File desktop = new File(new File(System.getProperty("user.home")), "Desktop");
        newFileExplorer(desktop);

        p.setBackground(new Color(51, 153, 255));

        JMenuBar b = new JMenuBar();
        b.add(new JMenu("Exit")).addMouseListener(new MouseClick() { @Override public void click(MouseEvent e) {System.exit(0);}});
        b.add(new JMenu("About " + NAME)).addMouseListener(new MouseClick(){ @Override public void click(MouseEvent e) {about();}});

        JMenuItem wb = programs.add("Web Browser");
        wb.addActionListener((l) -> startBrowser());
        wb.setEnabled(ram >= 200);

        JMenuItem fe = programs.add("File Explorer");
        fe.addActionListener((l) -> { 
            File dir = desktop;
            if (dir.getParent() != null) dir = dir.getParentFile();
            newFileExplorer(dir); 
        });

        programs.add("NotePad").addActionListener((l) -> emptyNotePad());
        programs.add("Console").addActionListener((l) -> start(new Console(), 600, 600));
        programs.add("Task Manager").addActionListener((l) -> {
            try { taskManager(); } catch (IOException | InterruptedException e1) { e1.printStackTrace(); }
        });
        programs.add("Lite Web Browser").addActionListener((l) -> start(new MiniBrowser(), 300, 500));
        programs.add("Calcalator").addActionListener((l) -> start(new Calc(), 200, 200));
        programs.add("Minesweeper").addActionListener((l) -> start(new MineSweeper(), 250, 350));

        if (pStorage.exists()) {
            try {
                FileInputStream fis = new FileInputStream(pStorage);
                ObjectInputStream ois = new ObjectInputStream(fis);
                pr = (ArrayList<String>) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }
        for (String s : pr) {
            try {
                pm.loadProgram(new File(s));
            } catch (NullPointerException e) {
                System.out.println("Null: " + s);
            }
        }

        b.add(programs);
        f.setJMenuBar(b);

        p.setVisible(true);
        f.setContentPane(p);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setUndecorated(true);
        f.setMaximumSize(new Dimension(500,500));
        f.pack();
        f.setVisible(true);
        pm = new JProgramManager();

        String l = getUrlSource("https://api.github.com/repos/isaiahpatton/shell/releases/latest");
        if (!l.equalsIgnoreCase("internet")) {
            l = l.substring(l.indexOf("\"tag_name\":\"") + 12);
            l = l.substring(0, l.indexOf("\","));
        } else showNotification("Could not connect to api.github.com\nto get update infomation.", new Font("Arial", Font.PLAIN, 13),
                5000, 320, 60);

        if (!l.equalsIgnoreCase("internet") && !l.equalsIgnoreCase(VERSION))
            showNotification("A new Update is ready to be downloaded!\n  Current version: " + VERSION + "\n  Latest version: "
                    + l + "\nhttp://isaiahpatton.github.io/jshell", new Font("Arial", Font.BOLD, 14), 10000, 420, 110);
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
        showNotification(tex, ms, 420, 110);
    }

    public static void showNotification(String tex, int ms, int width, int height) {
        showNotification(tex, new Font("Arial", Font.PLAIN, 13), ms, width, height);
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
        Notification note = new Notification(n, WindowPosition.BOTTOMRIGHT, 0, 0, ms);
        NotificationQueue queue = new NotificationQueue();
        queue.add(note);
    }

    public static String getInfo() {
        return "Version " + VERSION + " on Java " + System.getProperty("java.version") + "\n"
                + "Installed RAM: " + mem
                + "\n\nMade possible by:\n"
                + " - Calculator @ javacodex.com\n - MineSweeper @ java2s.com\n"
                + " - JCarrierPigeon @ carrierpigeon.sf.net";
    }

    protected static void newFileExplorer(File file) {
        if (file.isDirectory()) {
            int le = file.listFiles().length;
            JPanel pan = new JPanel(new GridLayout(le > 5 ? le / 5 : 3, 1));
            JProgram inf = new JProgram("[File Explorer] " + file.getName(), true , true, true);
            JPanel pa = new JPanel();

            JTextField field = new JTextField(file.getAbsolutePath());
            JButton back = new JButton("<");
            back.addActionListener((a) -> {
                File f = new File(field.getText());
                if (f.isDirectory() && f.getParent() != null) field.setText(f.getParentFile().getAbsolutePath());
                field.getKeyListeners()[0].keyReleased(null);
            });
            JPanel pa2 = new JPanel();
            pa2.add(back, BorderLayout.WEST);
            pa2.add(field, BorderLayout.EAST);
            back.setPreferredSize(new Dimension(back.getPreferredSize().width, field.getHeight() - 30));
            pa2.setLayout(new BoxLayout(pa2, BoxLayout.X_AXIS));
            pa.add(pa2);
 
            for (File fil : file.listFiles()) {
                Icon ic = new Icon(fil.getName(), fil.isDirectory());
                ic.addActionListener((l) -> {
                    if (fil.isDirectory()) {
                        field.setText(fil.getAbsolutePath());
                        field.getKeyListeners()[0].keyReleased(null);
                    } else newFileExplorer(fil);
                });
                new DragListener(ic).addHandle(ic);
                ic.setMaximumSize(new Dimension(200, 200));
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
                            boolean max = inf.isMaximum();
                            pan.removeAll();
                            pan.validate();
                            int le2 = z.listFiles().length;
                            pan.setLayout(new GridLayout(le2 > 5 ? le2 / 5 : 3, 1));
                            if (!max) inf.setSize(pa.getSize());
                            for (File fi : z.listFiles()) {
                                Icon ic = new Icon(fi.getName(), fi.isDirectory());
                                ic.addActionListener((l) -> {
                                    if (fi.isDirectory()) {
                                        field.setText(fi.getAbsolutePath());
                                        field.getKeyListeners()[0].keyReleased(null);
                                    } else newFileExplorer(fi);
                                });
                                new DragListener(ic).addHandle(ic);
                                pan.add(ic);
                            }
                            inf.pack();
                            pan.validate();
                            if (max) try { inf.setMaximum(true); } catch (PropertyVetoException e1) { e1.printStackTrace(); }
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
            if (name.endsWith(".jar")) pm.loadProgram(file, true);
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
        String text = "";
        int i = 0;
        if (file != null) for (String s : Files.readAllLines(file.toPath())) {
            if (i == 1) text += "\n" + s;
            if (i == 0) {
                text += s;
                i++;
            }
        }

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
        System.out.println("Starting ZunoZap");
        Platform.runLater(() -> { try { Browser.main(null); } catch (Exception e) { showNotification(e.getMessage(), 5000);
            e.printStackTrace(); }});
    }

    private static final void taskManager() throws IOException, InterruptedException {
        JProgram inf = new JProgram("Task Manager");
        JPanel pan = new JPanel();
        JTextArea a = new JTextArea(getTasks());
        JScrollPane bar = new JScrollPane();
        bar.setViewportView(a);
        a.setMargin(new Insets(0, 5, 5, 5));
        ((DefaultCaret)a.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        new Timer(1000, (l) -> {
            try { a.setText(getTasks()); } catch (IOException | InterruptedException e) { e.printStackTrace(); }
        }).start(); 
        a.setEditable(false);
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        pan.add(bar);
        inf.setContentPane(pan);
        start(inf, 550, 350);
    }

    private static final String getTasks() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist").redirectErrorStream(true);
        Process process = processBuilder.start();
        String s = "";
        try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null) s += readLine + "\n";

            process.waitFor();
        }
        process.destroy();
        return s;
    }

    private static final void about() {
        JProgram inf = new JProgram("About");
        JPanel pan = new JPanel();
        JTextArea n = new JTextArea(" " + NAME);
        n.setFont(new Font("Arial", Font.BOLD, 29));
        JTextArea a = new JTextArea(getInfo());
        n.setEditable(false);
        a.setEditable(false);
        a.setMargin(new Insets(0, 5, 5, 5));
        n.setMargin(new Insets(5, 5, 0, 5));
        n.setMaximumSize(new Dimension(50000, 250));
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        pan.add(n);
        pan.add(a);
        inf.setSize(250, 250);
        inf.setContentPane(pan);
        inf.setVisible(true);
        p.add(inf);
    }

    private static final void start(JProgram j, int width, int height) {
        j.setSize(width, height);
        j.setVisible(true);
        p.add(j);
    }
}
package me.isaiah.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import me.isaiah.shell.api.JProgram;
import me.isaiah.shell.api.JProgramInfo;
import me.isaiah.zunozap.plugin.PluginBase;

public class JProgramManager {
    public ArrayList<PluginBase> plugins = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();
    public ProgramClassLoader classLoader;

    public void loadProgram(File f) {
        loadProgram(f, false);
    }

    public void loadProgram(File f, boolean b) {
        if (!f.isDirectory() && f.getName().endsWith(".jar")) {
            try (JarFile jar = new JarFile(f)) {
                Manifest m = jar.getManifest();
                String main = m.getMainAttributes().getValue(Name.MAIN_CLASS);
                classLoader = new ProgramClassLoader(new ProgramLoader(), getClass().getClassLoader(), main, f);
                JProgram program = classLoader.plugin;
                JProgramInfo i = program.getClass().getAnnotation(JProgramInfo.class);
                JProgramInfo info = i == null ? info = DemoInfo.class.getAnnotation(JProgramInfo.class) : i;

                if (!Main.pr.contains(f.getAbsolutePath())) {
                    Main.pStorage.getParentFile().mkdirs();
                    Main.pStorage.createNewFile();
                    Main.pr.add(f.getAbsolutePath());
                    Main.showNotification("Registered \"" + info.name() + "\" to Programs menu", 5000, 300, 60);
                }
                Main.programs.add(info.name() + " " + info.version()).addActionListener((l) -> {
                    program.setVisible(true);
                    program.setSize(info.width(), info.height());
                    Main.p.add(program);
                });
                if (b) {
                    program.setVisible(true);
                    program.setSize(info.width(), info.height());
                    Main.p.add(program);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(f.getName() + " - Error with launching program!");
            }
        }
    }
}
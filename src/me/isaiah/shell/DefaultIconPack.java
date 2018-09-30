package me.isaiah.shell;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DefaultIconPack {

    public Image folder;
    public Image blank;

    public DefaultIconPack() {
        try {
            this.folder = get("folder.png");
            this.blank = get("blankfile.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image get(String name) throws IOException {
        return ImageIO.read(Icon.class.getClassLoader().getResourceAsStream(name)).getScaledInstance(40, 40, 0);
    }

}
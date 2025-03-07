package net.ovydon.serverManager.model;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class TOMLFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".toml");
    }

    @Override
    public String getDescription() {
        return "TOML config file (*.toml)";
    }
}

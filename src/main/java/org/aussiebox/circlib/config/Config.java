package org.aussiebox.circlib.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    public final Identifier identifier;
    public final List<String> folders;

    public Config(Identifier identifier) {
        this.identifier = identifier;
        this.folders = new ArrayList<>();
    }

    public Config(Identifier identifier, String... folders) {
        this.identifier = identifier;
        this.folders = new ArrayList<>(List.of(folders));
    }

    public final File getDirectory() {
        StringBuilder subfolder = new StringBuilder(Strings.EMPTY);
        for (String folder : folders) {
            subfolder.append(folder);
            if (!Objects.equals(folders.getLast(), folder)) subfolder.append(File.pathSeparator);
        }
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), subfolder.toString());
    }
}

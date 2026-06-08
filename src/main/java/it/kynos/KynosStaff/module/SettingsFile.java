package it.kynos.KynosStaff.module;

import it.kynos.kynoslib.files.KynosFile;
import it.kynos.kynoslib.utils.ColorUtils;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsFile extends KynosFile {

    public SettingsFile(JavaPlugin plugin) {
        super(plugin, "settings.yml");
    }

    public void reloadSettings() {
        reload();
    }

    public String getSetting(String path) {
        String message = getConfig().getString(path);

        if (message == null) {
            return "§c[Missing setting in settings.yml: " + path + "]";
        }
        return ColorUtils.translateToString(message);
    }
}
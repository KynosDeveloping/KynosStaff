package it.kynos.KynosStaff.module;

import it.kynos.kynoslib.files.KynosFile;
import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.stream.Collectors;

public class LangFile extends KynosFile {

    public final MessageUtils msg;

    public LangFile(JavaPlugin plugin, MessageUtils msg) {
        super(plugin, "lang.yml");
        this.msg = msg;
    }

    public void reloadLang() {
        reload();
    }

    public String getMessage(String path) {
        String message = getConfig().getString(path);

        if (message == null) {
            return "§c[Missing message in lang.yml: " + path + "]";
        }

        if (msg == null) {
            return ColorUtils.translateToString(message);
        }
        return ColorUtils.translateToString(message);
    }

    public String getMessage(String path, Player player) {
        String message = getConfig().getString(path);

        if (message == null) {
            return "§c[Missing message in lang.yml: " + path + "]";
        }

        if (player != null) {
            message = message.replace("{player}", player.getName());
        }

        if (org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                Class<?> papiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                java.lang.reflect.Method setPlaceholdersMethod = papiClass.getMethod("setPlaceholders", Player.class, String.class);
                message = (String) setPlaceholdersMethod.invoke(null, player, message);
            } catch (Exception e) {
            }
        }
        return ColorUtils.translateToString(message, player);
    }

    public List<String> getMessageList(String path) {
        List<String> list = getConfig().getStringList(path);
        return list.stream()
                .map(ColorUtils::translateToString)
                .collect(Collectors.toList());
    }
}
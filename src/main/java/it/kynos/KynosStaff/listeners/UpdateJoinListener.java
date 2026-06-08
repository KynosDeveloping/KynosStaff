package it.kynos.KynosStaff.listeners;

import it.kynos.KynosStaff.utils.GitHubUpdater;
import it.kynos.kynoslib.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateJoinListener implements Listener {

    private final JavaPlugin plugin;
    private final GitHubUpdater updater;

    public UpdateJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.updater = new GitHubUpdater(plugin, "KynosDeveloping", "KynosStaff");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("kynosstaff.staffpanel.use")) {
            return;
        }
        updater.checkForUpdate().thenAccept(latestVersion -> {
            if (latestVersion != null) {
                player.sendMessage(ColorUtils.translate("&#ff8585⚠ KynosStaff &8• &7A new update is available! Version: &#ffdf7a" + latestVersion));
                player.sendMessage(ColorUtils.translate("&#ff8585➔ &7Download it from your GitHub Releases repository."));
            } else {
                if (plugin.getConfig().getBoolean("Settings.update-checker", true)) {
                    player.sendMessage(ColorUtils.translate("&#0cb16e✔ KynosStaff &f• You are running the latest version &#59e2e2(" + plugin.getDescription().getVersion() + ")."));
                }
            }
        });
    }
}
package it.kynos.KynosStaff.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class FreezeListener implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey freezeKey;

    public FreezeListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.freezeKey = new NamespacedKey(plugin, "frozen");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getPersistentDataContainer().has(freezeKey, PersistentDataType.BYTE)) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        boolean blockCommands = plugin.getConfig().getBoolean("Freeze.block-commands", true);
        if (!blockCommands) {
            return;
        }
        if (player.getPersistentDataContainer().has(freezeKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
        }
    }
}
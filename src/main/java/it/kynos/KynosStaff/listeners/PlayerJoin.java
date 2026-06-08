package it.kynos.KynosStaff.listeners;

import it.kynos.KynosStaff.KynosStaff;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joined = event.getPlayer();
        if (!joined.hasPermission("kynosstaff.panel.use")) {
            for (Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasMetadata("vanished")) {
                    joined.hidePlayer(KynosStaff.getInstance(), onlinePlayer);
                }
            }
        }
    }
}

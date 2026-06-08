package it.kynos.KynosStaff.listeners;

import it.kynos.KynosStaff.gui.MainStaffGui;
import it.kynos.KynosStaff.module.LangFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final LangFile langFile;

    public ChatListener(LangFile langFile) {
        this.langFile = langFile;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!MainStaffGui.isChatMuted) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("kynosstaff.chat.bypass")) {
            event.setCancelled(true);
            player.sendMessage(langFile.getMessage("Messages.chat-is-currently-muted"));
        }
    }
}
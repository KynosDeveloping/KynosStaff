package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.gui.MainStaffGui;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Global chat toggle command. Interacts with the MainStaffGui state tracker.
 */
public class MuteChatCommand extends KynosCommand {

    private final LangFile langFile;

    public MuteChatCommand(KynosStaff plugin, String commandName, LangFile langFile) {
        super(plugin, commandName);
        this.langFile = langFile;
        permission("kynosstaff.mutechat");
        // set to false: Allows RCON or Console logs to override/mute chat during emergencies
        requirePlayer(false);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        // Toggles the static boolean flag checked inside AsyncPlayerChatEvent
        MainStaffGui.isChatMuted = !MainStaffGui.isChatMuted;
        String stafferName = sender.getName();

        // Dynamically path routes and broadcasts system localization messages to the whole cluster
        String path = MainStaffGui.isChatMuted ? "Messages.globalchat-muted" : "Messages.globalchat-unmuted";
        Bukkit.broadcastMessage(langFile.getMessage(path).replace("%staffer%", stafferName));
        return true;
    }
}
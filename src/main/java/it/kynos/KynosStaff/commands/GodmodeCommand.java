package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command class providing absolute damage immunity (Godmode) to staff members.
 */
public class GodmodeCommand extends KynosCommand {

    private final LangFile langFile;

    public GodmodeCommand(KynosStaff plugin, String commandName, LangFile langFile) {
        super(plugin, commandName);
        this.langFile = langFile;
        permission("kynosstaff.godmode");
        requirePlayer(true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        // Uses Bukkit's built-in invulnerability layer to block all hazard/environmental damage ticks
        if (p.isInvulnerable()) {
            p.setInvulnerable(false);
            p.sendMessage(langFile.getMessage("Messages.godmodeoff"));
        } else {
            p.setInvulnerable(true);
            p.sendMessage(langFile.getMessage("Messages.godmodeon"));
        }
        return true;
    }
}
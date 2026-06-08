package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command class responsible for toggling flight mode for staff members.
 * Extends KynosCommand to inherit automated player and permission checks.
 */
public class FlyCommand extends KynosCommand {

    private final LangFile langFile;

    public FlyCommand(KynosStaff plugin, String commandName, LangFile langFile) {
        super(plugin, commandName);
        this.langFile = langFile;
        // Restricts this command to users with the following permission node
        permission("kynosstaff.fly");
        // Block console execution automatically via the library framework
        requirePlayer(true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        // Toggle flight states based on current state
        if (!p.getAllowFlight()) {
            p.setAllowFlight(true);
            p.setFlying(true);
            // 0.2F is the default Minecraft flight speed. Modify this float to change speed.
            p.setFlySpeed(0.2F);
            p.sendMessage(langFile.getMessage("Messages.flyon"));
        } else {
            p.setFlying(false);
            p.setAllowFlight(false);
            // 0.1F is the default Minecraft walking/fallback speed.
            p.setFlySpeed(0.1F);
            p.sendMessage(langFile.getMessage("Messages.flyoff"));
        }
        return true;
    }
}
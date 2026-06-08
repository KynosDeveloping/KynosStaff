package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Command class to handle packet masking/hiding player entities from normal clients.
 */
public class VanishCommand extends KynosCommand {

    private final KynosStaff plugin;
    private final LangFile langFile;

    public VanishCommand(KynosStaff plugin, String commandName, LangFile langFile) {
        super(plugin, commandName);
        this.plugin = plugin;
        this.langFile = langFile;
        permission("kynosstaff.vanish");
        requirePlayer(true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        // If metadata tag exists, player is currently vanished -> reveal them
        if (p.hasMetadata("vanished")) {
            p.removeMetadata("vanished", plugin);
            p.setInvulnerable(false);

            // Loop through all active cluster nodes and restore standard entity rendering frames
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, p);
            }
            p.sendMessage(langFile.getMessage("Messages.vanishoff"));
        } else {
            // Inject metadata object tag onto the player entity instance for session state caching
            p.setMetadata("vanished", new FixedMetadataValue(plugin, true));
            p.setInvulnerable(true); // Optional: keeps vanish user invulnerable while hidden

            // Mask packets loop: hides the player from normal users, but allows staff peers to still see them
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("kynosstaff.panel.use")) {
                    onlinePlayer.hidePlayer(plugin, p);
                } else {
                    onlinePlayer.showPlayer(plugin, p);
                }
            }
            p.sendMessage(langFile.getMessage("Messages.vanishon"));
        }
        return true;
    }
}
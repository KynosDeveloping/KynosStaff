package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.module.LangFile;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

/**
 * Command class to lock a player's positional vectors (Freeze).
 * Uses PersistentDataContainer (PDC) to ensure data persists through logouts or reloads.
 */
public class FreezeCommand extends KynosCommand {

    private final LangFile langFile;
    private final NamespacedKey freezeKey;

    public FreezeCommand(JavaPlugin plugin, String commandName, LangFile langFile) {
        super(plugin, commandName);
        this.langFile = langFile;
        permission("kynosstaff.freeze");
        requirePlayer(true);
        // NamespaceKey used to identify the persistent data tag on the player entity
        this.freezeKey = new NamespacedKey(plugin, "frozen");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        // Validation: Ensure an argument is provided (the target player's name)
        if (args.length < 1) {
            sender.sendMessage(langFile.getMessage("Messages.provideplayer"));
            return true;
        }

        // Fetch the target player dynamically from the online cache
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(langFile.getMessage("Messages.player-offline"));
            return true;
        }

        // Check PDC: If key exists, unfreeze them. If it doesn't, freeze them.
        if (target.getPersistentDataContainer().has(freezeKey, PersistentDataType.BYTE)) {
            target.getPersistentDataContainer().remove(freezeKey);
            target.sendMessage(langFile.getMessage("Messages.target-unfrozen"));
            sender.sendMessage(langFile.getMessage("Messages.staff-unfrozen-success").replace("%target%", target.getName()));
        } else {
            // Byte (1) acts as a persistent boolean flag inside the entity data
            target.getPersistentDataContainer().set(freezeKey, PersistentDataType.BYTE, (byte) 1);
            target.sendMessage(langFile.getMessage("Messages.target-frozen"));
            sender.sendMessage(langFile.getMessage("Messages.staff-frozen-success").replace("%target%", target.getName()));
        }
        return true;
    }

    /**
     * Tabcompletion system to dynamically suggest online players matching the current input arg.
     */
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
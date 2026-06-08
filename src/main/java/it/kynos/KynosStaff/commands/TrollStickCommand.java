package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

/**
 * Instantiates a testing stick weapon amplified with extreme custom knockback parameters.
 */
public class TrollStickCommand extends KynosCommand {

    private final LangFile langFile;

    public TrollStickCommand(KynosStaff plugin, String commandName, LangFile langFile) {
        super(plugin, commandName);
        this.langFile = langFile;
        permission("kynosstaff.trollstick");
        requirePlayer(true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int knockbackLevel = 4; // Default safe fallback knockback depth factor

        // Argument parsing block: Checks if a localized level overwrite parameter was specified
        if (args.length >= 1) {
            try {
                knockbackLevel = Integer.parseInt(args[0]);
                if (knockbackLevel <= 0) {
                    p.sendMessage(langFile.getMessage("Messages.invalid-level-range"));
                    return true;
                }
            } catch (NumberFormatException e) {
                // Failsafe catch block to prevent stack trace exceptions if input string isn't an integer mapping
                p.sendMessage(langFile.getMessage("Messages.invalid-number"));
                return true;
            }
        }

        // Itemstack building factory
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta stickmeta = stick.getItemMeta();

        if (stickmeta != null) {
            // true flag ignores standard vanilla client level restriction wrappers (allows levels > 2)
            stickmeta.addEnchant(Enchantment.KNOCKBACK, knockbackLevel, true);
            stick.setItemMeta(stickmeta);
        }

        // Inject the freshly instanced item directly into the player entity's active inventory frame
        p.getInventory().addItem(stick);
        p.sendMessage(langFile.getMessage("Messages.trollstick-received").replace("%level%", String.valueOf(knockbackLevel)));
        return true;
    }

    /**
     * Provide rapid preset power magnitude suggestions for easier usage.
     */
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("1", "4", "10", "50", "100");
        }
        return List.of();
    }
}
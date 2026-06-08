package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.KynosStaff.module.SettingsFile;
import it.kynos.kynoslib.commands.KynosCommand;
import it.kynos.kynoslib.utils.ColorUtils; // IMPORTANTE: Importiamo la utility dei colori
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class KynosStaffCommand extends KynosCommand {

    private final KynosStaff plugin;
    private final SettingsFile settingsFile;
    private final LangFile langFile;

    public KynosStaffCommand(KynosStaff plugin, String commandName, LangFile langFile, SettingsFile settingsFile) {
        super(plugin, commandName);
        this.plugin = plugin;
        this.settingsFile = settingsFile;
        this.langFile = langFile;
        requirePlayer(false);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("");
        sender.sendMessage(ColorUtils.translateToString("&#f3b23e&lKynosStaff &8• &7Plugin Information"));
        sender.sendMessage(ColorUtils.translateToString(" &#0cb16e➔ &7Version: &#59e2e2" + plugin.getDescription().getVersion()));
        sender.sendMessage(ColorUtils.translateToString(" &#0cb16e➔ &7Author: &#59e2e2" + String.join(", ", plugin.getDescription().getAuthors())));
        sender.sendMessage(ColorUtils.translateToString(" &#0cb16e➔ &7Status: &#a3e043Online"));
        if (sender.hasPermission("kynosstaff.staff.help")) {
            sender.sendMessage("");
            sender.sendMessage(ColorUtils.translateToString(" &8» &7Use &#ffdf7a/kynosstaff help &7to view admin commands."));
        }
        sender.sendMessage("");
        return true;
    }

    @SubCommand(name = "help", permission = "kynosstaff.staff.help", playerOnly = false)
    public boolean help(CommandSender sender, String[] args) {
        List<String> helpLines = langFile.getMessageList("Messages.HelpMenu");

        for (String line : helpLines) {
            sender.sendMessage(line);
        }
        return true;
    }

    @SubCommand(name = "reloadlang", permission = "kynosstaff.staff.reloadlang", playerOnly = false)
    public boolean reloadlang(CommandSender sender, String[] args) {
        langFile.reloadLang();
        if (sender instanceof Player p) {
            p.sendTitle(langFile.getMessage("Messages.Reload.lang-title"), langFile.getMessage("Messages.Reload.lang-subtitle"), 10, 40, 10);
        } else {
            sender.sendMessage(ColorUtils.translateToString("&aLang.yml successfully reloaded."));
        }
        return true;
    }

    @SubCommand(name = "reloadsettings", permission = "kynosstaff.staff.reloadsettings", playerOnly = false)
    public boolean reloadsettings(CommandSender sender, String[] args) {
        settingsFile.reloadSettings();
        if (sender instanceof Player p) {
            p.sendTitle(langFile.getMessage("Messages.Reload.settings-title"), langFile.getMessage("Messages.Reload.settings-subtitle"), 10, 40, 10);
        } else {
            sender.sendMessage(ColorUtils.translateToString("&aSettings.yml successfully reloaded."));
        }
        return true;
    }

    @SubCommand(name = "reloadconfig", permission = "kynosstaff.staff.reloadconfig", playerOnly = false)
    public boolean reloadconfig(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        if (sender instanceof Player p) {
            p.sendTitle(langFile.getMessage("Messages.Reload.config-title"), langFile.getMessage("Messages.Reload.config-subtitle"), 10, 40, 10);
        } else {
            sender.sendMessage(ColorUtils.translateToString("&aConfig.yml successfully reloaded."));
        }
        return true;
    }
}
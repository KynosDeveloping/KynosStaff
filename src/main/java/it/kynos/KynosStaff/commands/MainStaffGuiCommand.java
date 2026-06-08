package it.kynos.KynosStaff.commands;

import it.kynos.KynosStaff.KynosStaff;
import it.kynos.KynosStaff.gui.MainStaffGui;
import it.kynos.kynoslib.commands.KynosCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainStaffGuiCommand extends KynosCommand {

    private final KynosStaff plugin;

    public MainStaffGuiCommand(KynosStaff plugin, String commandName) {
        super(plugin, commandName);
        this.plugin = plugin;
        permission("kynosstaff.panel.use");
        requirePlayer(true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        MainStaffGui staffGui = new MainStaffGui(plugin.getLangFile(), plugin.getSettingsFile(), player, 0);
        staffGui.open(player);
        return true;
    }
}
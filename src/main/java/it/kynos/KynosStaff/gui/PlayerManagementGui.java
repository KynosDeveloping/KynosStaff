package it.kynos.KynosStaff.gui;

import it.kynos.KynosStaff.module.LangFile;
import it.kynos.KynosStaff.module.SettingsFile;
import it.kynos.kynoslib.menu.KynosGui;
import it.kynos.kynoslib.utils.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerManagementGui extends KynosGui {

    private final LangFile langFile;
    private final SettingsFile settingsFile;
    private final Player target;

    public PlayerManagementGui(LangFile langFile, SettingsFile settingsFile, Player staffer, Player target) {
        super(54, langFile.getMessage("GUI.PlayerManagementGui.title").replace("{target}", target.getName()));
        this.langFile = langFile;
        this.settingsFile = settingsFile;
        this.target = target;

        int headSlot = getSafeSlot("Settings.MainGui.staffheadslot", 0);
        int listSlot = getSafeSlot("Settings.MainGui.playerlistslot", 9);
        int muteChatSlot = getSafeSlot("Settings.MainGui.globalmute-slot", 18);
        int quickRtpSlot = getSafeSlot("Settings.MainGui.quickrtp-slot", 27);
        int closeSlot = getSafeSlot("Settings.MainGui.close-button-slot", 45);

        ItemStack staffhead = buildItem(Material.PLAYER_HEAD, langFile.getMessage("GUI.Maingui.staff-head.name", staffer), langFile.getMessageList("GUI.Maingui.staff-head.lore"));
        ItemStack playerlist = buildItem(Material.HEART_OF_THE_SEA, langFile.getMessage("GUI.Maingui.playerlist.name"), langFile.getMessageList("GUI.Maingui.playerlist.lore"));
        if (staffhead.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(staffer);
            staffhead.setItemMeta(skullMeta);
        }
        this.inventory.setItem(headSlot, staffhead);
        this.inventory.setItem(listSlot, playerlist);

        String muteMatName = settingsFile.getSetting(MainStaffGui.isChatMuted ? "Settings.MainGui.globalmute.material-muted" : "Settings.MainGui.globalmute.material-unmuted");
        String muteName = langFile.getMessage(MainStaffGui.isChatMuted ? "GUI.Maingui.globalmute.name-muted" : "GUI.Maingui.globalmute.name-unmuted");
        Material muteMaterial;
        try {
            muteMaterial = Material.valueOf(muteMatName.toUpperCase());
        } catch (Exception e) {
            muteMaterial = MainStaffGui.isChatMuted ? Material.RED_DYE : Material.LIME_DYE;
        }
        this.inventory.setItem(muteChatSlot, buildItem(muteMaterial, muteName, langFile.getMessageList("GUI.MainGui.globalmute.lore")));

        String rtpMatName = settingsFile.getSetting("Settings.MainGui.quickrtp.material");
        Material rtpMaterial;
        try {
            rtpMaterial = Material.valueOf(rtpMatName.toUpperCase());
        } catch (Exception e) {
            rtpMaterial = Material.COMPASS;
        }
        this.inventory.setItem(quickRtpSlot, buildItem(rtpMaterial, langFile.getMessage("GUI.Maingui.quickrtp.name"), langFile.getMessageList("GUI.Maingui.quickrtp.lore")));

        String glassMaterialName = settingsFile.getSetting("Settings.MainGui.filler-glass-material");
        Material glassMaterial;
        try {
            glassMaterial = Material.valueOf(glassMaterialName.toUpperCase());
        } catch (Exception e) {
            glassMaterial = Material.GRAY_STAINED_GLASS_PANE;
        }
        ItemStack glassPane = buildItem(glassMaterial, " ");
        int glassCmd = getSafeSlot("Settings.MainGui.filler-glass-custommodeldata", 0);
        if (glassCmd > 0 && glassPane.getItemMeta() != null) {
            ItemMeta meta = glassPane.getItemMeta();
            meta.setCustomModelData(glassCmd);
            glassPane.setItemMeta(meta);
        }
        for (int i = 1; i < 54; i += 9) {
            this.inventory.setItem(i, glassPane);
        }
        this.inventory.setItem(36, glassPane);

        String closeMatName = settingsFile.getSetting("Settings.MainGui.close-button-material");
        Material closeMaterial;
        try {
            closeMaterial = Material.valueOf(closeMatName.toUpperCase());
        } catch (Exception e) {
            closeMaterial = Material.BARRIER;
        }
        ItemStack closeItem = buildItem(closeMaterial, langFile.getMessage("GUI.Global.close-button.name"), langFile.getMessageList("GUI.Global.close-button.lore"));
        int closeCmd = getSafeSlot("Settings.MainGui.close-button-custommodeldata", 0);
        if (closeCmd > 0 && closeItem.getItemMeta() != null) {
            ItemMeta meta = closeItem.getItemMeta();
            meta.setCustomModelData(closeCmd);
            closeItem.setItemMeta(meta);
        }
        this.inventory.setItem(closeSlot, closeItem);

        setupConfiguredItem("Settings.MainGui.playermanagement.teleport", "GUI.PlayerManagementGui.items.teleport");
        setupConfiguredItem("Settings.MainGui.playermanagement.freeze", "GUI.PlayerManagementGui.items.freeze");
        setupConfiguredItem("Settings.MainGui.playermanagement.invsee", "GUI.PlayerManagementGui.items.invsee");
        setupConfiguredItem("Settings.MainGui.playermanagement.kick", "GUI.PlayerManagementGui.items.kick");
        setupConfiguredItem("Settings.MainGui.playermanagement.mute", "GUI.PlayerManagementGui.items.mute");
        setupConfiguredItem("Settings.MainGui.playermanagement.enderchest", "GUI.PlayerManagementGui.items.enderchest");
        setupConfiguredItem("Settings.MainGui.playermanagement.kill", "GUI.PlayerManagementGui.items.kill");

        fillEmptySlotsWithGlass("Settings.MainGui.playermanagement.fill-empty-slots", glassPane);
    }

    @Override
    public void handleSlotClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player staffer = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();

        if (clickedSlot == getSafeSlot("Settings.MainGui.close-button-slot", 45)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.CloseMenu");
            staffer.closeInventory();
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.staffheadslot", 0)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            new AutomanagementGui(langFile, settingsFile, staffer).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playerlistslot", 9)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            new PlayerListGui(langFile, settingsFile, staffer, 0).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.globalmute-slot", 18)) {
            MainStaffGui.isChatMuted = !MainStaffGui.isChatMuted;
            if (MainStaffGui.isChatMuted) {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ToggleMuteOn");
            } else {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ToggleMuteOff");
            }
            String msgKey = MainStaffGui.isChatMuted ? "Messages.globalchat-muted" : "Messages.globalchat-unmuted";
            Bukkit.broadcastMessage(langFile.getMessage(msgKey).replace("%staffer%", staffer.getName()));
            new PlayerManagementGui(langFile, settingsFile, staffer, target).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.quickrtp-slot", 27)) {
            handleQuickRtp(staffer);
            return;
        }

        if (target == null || !target.isOnline()) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
            staffer.sendMessage(langFile.getMessage("Messages.player-offline"));
            staffer.closeInventory();
            return;
        }

        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.teleport.slot", 12)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.TeleportAction");
            staffer.teleport(target.getLocation());
            staffer.sendMessage(langFile.getMessage("Messages.teleported-to-staffer").replace("%target%", target.getName()));
            staffer.closeInventory();
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.freeze.slot", 14)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.PunishAction");
            String cmd = settingsFile.getSetting("Settings.MainGui.playermanagement.freeze.command");
            if (cmd != null && !cmd.isEmpty()) {
                staffer.performCommand(cmd.replace("%target%", target.getName()));
            }
            staffer.closeInventory();
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.invsee.slot", 16)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            staffer.openInventory(target.getInventory());
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.kick.slot", 23)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.PunishAction");
            String cmd = settingsFile.getSetting("Settings.MainGui.playermanagement.kick.command");
            if (cmd != null && !cmd.isEmpty()) {
                staffer.performCommand(cmd.replace("%target%", target.getName()));
            }
            staffer.closeInventory();
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.mute.slot", 30)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            new TimeSelectionGui(langFile, settingsFile, staffer, target, "Settings.MainGui.playermanagement.mute").open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.enderchest.slot", 32)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            staffer.openInventory(target.getEnderChest());
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playermanagement.kill.slot", 34)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.PunishAction");
            target.setHealth(0.0D);
            staffer.sendMessage(langFile.getMessage("Messages.player-killed").replace("%target%", target.getName()));
            staffer.closeInventory();
        }
    }

    private void handleQuickRtp(Player staffer) {
        List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        targets.remove(staffer);
        if (targets.isEmpty()) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
            staffer.sendMessage(langFile.getMessage("Messages.no-players-online"));
            staffer.closeInventory();
            return;
        }
        SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.TeleportAction");
        Collections.shuffle(targets);
        Player randomPlayer = targets.get(0);
        staffer.teleport(randomPlayer.getLocation());
        staffer.sendMessage(langFile.getMessage("Messages.teleported-to-random").replace("%target%", randomPlayer.getName()));
        staffer.closeInventory();
    }

    private void fillEmptySlotsWithGlass(String configPath, ItemStack glassPane) {
        if (!Boolean.parseBoolean(settingsFile.getSetting(configPath))) return;
        int headSlot = getSafeSlot("Settings.MainGui.staffheadslot", 0);
        int listSlot = getSafeSlot("Settings.MainGui.playerlistslot", 9);
        int muteChatSlot = getSafeSlot("Settings.MainGui.globalmute-slot", 18);
        int quickRtpSlot = getSafeSlot("Settings.MainGui.quickrtp-slot", 27);
        int closeSlot = getSafeSlot("Settings.MainGui.close-button-slot", 45);

        for (int s = 0; s < 54; s++) {
            if (s == headSlot || s == listSlot || s == muteChatSlot || s == quickRtpSlot || s == closeSlot || (s % 9 == 1)) {
                continue;
            }
            if (this.inventory.getItem(s) == null) {
                this.inventory.setItem(s, glassPane);
            }
        }
    }

    private void setupConfiguredItem(String configPath, String langPath) {
        String matName = settingsFile.getSetting(configPath + ".material");
        Material material;
        try {
            material = Material.valueOf(matName.toUpperCase());
        } catch (Exception e) {
            material = Material.STONE;
        }
        ItemStack item = buildItem(material, langFile.getMessage(langPath + ".name"), langFile.getMessageList(langPath + ".lore"));
        int slot = getSafeSlot(configPath + ".slot", -1);
        if (slot >= 0 && slot < 54) {
            this.inventory.setItem(slot, item);
        }
    }

    private int getSafeSlot(String path, int defaultSlot) {
        try {
            return Integer.parseInt(settingsFile.getSetting(path));
        } catch (NumberFormatException e) {
            return defaultSlot;
        }
    }
}
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListGui extends KynosGui {

    private final LangFile langFile;
    private final SettingsFile settingsFile;
    private final Map<Integer, Player> slotToPlayerMap = new HashMap<>();
    private final int page;

    public PlayerListGui(LangFile langFile, SettingsFile settingsFile, Player player, int page) {
        super(54, langFile.getMessage("GUI.PlayerList.title").replace("{page}", String.valueOf(page + 1)));
        this.langFile = langFile;
        this.settingsFile = settingsFile;
        this.page = page;

        int headSlot = getSafeSlot("Settings.MainGui.staffheadslot", 0);
        int listSlot = getSafeSlot("Settings.MainGui.playerlistslot", 9);
        int muteChatSlot = getSafeSlot("Settings.MainGui.globalmute-slot", 18);
        int quickRtpSlot = getSafeSlot("Settings.MainGui.quickrtp-slot", 27);
        int closeSlot = getSafeSlot("Settings.MainGui.close-button-slot", 45);

        ItemStack staffhead = buildItem(Material.PLAYER_HEAD, langFile.getMessage("GUI.Maingui.staff-head.name", player), langFile.getMessageList("GUI.Maingui.staff-head.lore"));
        ItemStack playerlist = buildItem(Material.HEART_OF_THE_SEA, langFile.getMessage("GUI.Maingui.playerlist.name"), langFile.getMessageList("GUI.Maingui.playerlist.lore"));
        if (staffhead.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
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
        this.inventory.setItem(muteChatSlot, buildItem(muteMaterial, muteName, langFile.getMessageList("GUI.Maingui.globalmute.lore")));

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

        List<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Integer> allowedSlots = new ArrayList<>();
        for (int s = 0; s < 45; s++) {
            if (s == headSlot || s == listSlot || s == muteChatSlot || s == quickRtpSlot || (s % 9 == 1)) {
                continue;
            }
            allowedSlots.add(s);
        }

        int maxItemsPerPage = allowedSlots.size();
        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, allPlayers.size());

        String playerHeadNameTemplate = langFile.getMessage("GUI.PlayerList.player-head.name");
        List<String> playerHeadLore = langFile.getMessageList("GUI.PlayerList.player-head.lore");

        int slotPicker = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Player onlinePlayer = allPlayers.get(i);
            int targetSlot = allowedSlots.get(slotPicker);

            ItemStack playerHead = buildItem(Material.PLAYER_HEAD, playerHeadNameTemplate.replace("{player}", onlinePlayer.getName()), playerHeadLore);
            if (playerHead.getItemMeta() instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(onlinePlayer);
                playerHead.setItemMeta(skullMeta);
            }

            this.inventory.setItem(targetSlot, playerHead);
            slotToPlayerMap.put(targetSlot, onlinePlayer);
            slotPicker++;
        }

        int arrowCmd = getSafeSlot("Settings.MainGui.pagination-arrows-custommodeldata", 0);
        if (page > 0) {
            ItemStack prevArrow = buildItem(Material.ARROW, langFile.getMessage("GUI.Pagination.previous-page-name"));
            if (arrowCmd > 0 && prevArrow.getItemMeta() != null) {
                ItemMeta m = prevArrow.getItemMeta();
                m.setCustomModelData(arrowCmd);
                prevArrow.setItemMeta(m);
            }
            this.inventory.setItem(47, prevArrow);
        }
        if (endIndex < allPlayers.size()) {
            ItemStack nextArrow = buildItem(Material.ARROW, langFile.getMessage("GUI.Pagination.next-page-name"));
            if (arrowCmd > 0 && nextArrow.getItemMeta() != null) {
                ItemMeta m = nextArrow.getItemMeta();
                m.setCustomModelData(arrowCmd);
                nextArrow.setItemMeta(m);
            }
            this.inventory.setItem(53, nextArrow);
        }
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
        if (clickedSlot == 47 && page > 0) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ChangePage");
            new PlayerListGui(langFile, settingsFile, staffer, page - 1).open(staffer);
            return;
        }
        if (clickedSlot == 53 && this.inventory.getItem(53) != null) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ChangePage");
            new PlayerListGui(langFile, settingsFile, staffer, page + 1).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.staffheadslot", 0)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            new AutomanagementGui(langFile, settingsFile, staffer).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.playerlistslot", 9)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
            staffer.sendMessage(langFile.getMessage("Messages.alreadyonthegui"));
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
            new PlayerListGui(langFile, settingsFile, staffer, page).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.quickrtp-slot", 27)) {
            handleQuickRtp(staffer);
            return;
        }

        if (slotToPlayerMap.containsKey(clickedSlot)) {
            Player targetPlayer = slotToPlayerMap.get(clickedSlot);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
                staffer.sendMessage(langFile.getMessage("Messages.player-offline"));
                staffer.closeInventory();
                return;
            }
            if (targetPlayer.hasPermission("kynosstaff.panel.use")) {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
                staffer.sendMessage(langFile.getMessage("Messages.cannot-interact-with-staff"));
                return;
            }
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            new PlayerManagementGui(langFile, settingsFile, staffer, targetPlayer).open(staffer);
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

    private int getSafeSlot(String path, int defaultSlot) {
        try {
            return Integer.parseInt(settingsFile.getSetting(path));
        } catch (NumberFormatException e) {
            return defaultSlot;
        }
    }
}
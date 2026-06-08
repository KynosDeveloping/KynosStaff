package it.kynos.KynosStaff.gui;

import it.kynos.KynosStaff.module.LangFile;
import it.kynos.KynosStaff.module.SettingsFile;
import it.kynos.kynoslib.menu.KynosGui;
import it.kynos.kynoslib.utils.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainStaffGui extends KynosGui {

    public static boolean isChatMuted = false;
    private final LangFile langFile;
    private final SettingsFile settingsFile;
    private final Map<Integer, Player> slotToStafferMap = new HashMap<>();
    private final int page;

    public MainStaffGui(LangFile langFile, SettingsFile settingsFile, Player player, int page) {
        super(54, langFile.getMessage("GUI.Maingui.title").replace("{page}", String.valueOf(page + 1)));
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

        String muteMatName = settingsFile.getSetting(isChatMuted ? "Settings.MainGui.globalmute.material-muted" : "Settings.MainGui.globalmute.material-unmuted");
        String muteName = langFile.getMessage(isChatMuted ? "GUI.Maingui.globalmute.name-muted" : "GUI.Maingui.globalmute.name-unmuted");
        Material muteMaterial;
        try {
            muteMaterial = Material.valueOf(muteMatName.toUpperCase());
        } catch (Exception e) {
            muteMaterial = isChatMuted ? Material.RED_DYE : Material.LIME_DYE;
        }
        this.inventory.setItem(muteChatSlot, buildItem(muteMaterial, muteName, langFile.getMessageList("GUI.Maingui.globalmute.lore")));

        setupConfiguredItem("Settings.MainGui.quickrtp", "GUI.Maingui.quickrtp");

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

        List<Player> validStaffers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("kynosstaff.panel.use") && !onlinePlayer.equals(player)) {
                validStaffers.add(onlinePlayer);
            }
        }

        List<Integer> allowedSlots = new ArrayList<>();
        for (int s = 0; s < 45; s++) {
            if (s == headSlot || s == listSlot || s == muteChatSlot || s == quickRtpSlot || (s % 9 == 1)) {
                continue;
            }
            allowedSlots.add(s);
        }

        int maxItemsPerPage = allowedSlots.size();
        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, validStaffers.size());

        String staffHeadNameTemplate = langFile.getMessage("GUI.Maingui.other-staff.name");
        List<String> staffHeadLore = langFile.getMessageList("GUI.Maingui.other-staff.lore");

        int slotPicker = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Player onlinePlayer = validStaffers.get(i);
            int targetSlot = allowedSlots.get(slotPicker);

            ItemStack otherStaffHead = buildItem(Material.PLAYER_HEAD, staffHeadNameTemplate.replace("%staffer%", onlinePlayer.getName()), staffHeadLore);
            if (otherStaffHead.getItemMeta() instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(onlinePlayer);
                otherStaffHead.setItemMeta(skullMeta);
            }

            this.inventory.setItem(targetSlot, otherStaffHead);
            slotToStafferMap.put(targetSlot, onlinePlayer);
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
        if (endIndex < validStaffers.size()) {
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
            new MainStaffGui(langFile, settingsFile, staffer, page - 1).open(staffer);
            return;
        }
        if (clickedSlot == 53 && this.inventory.getItem(53) != null) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ChangePage");
            new MainStaffGui(langFile, settingsFile, staffer, page + 1).open(staffer);
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
            isChatMuted = !isChatMuted;
            if (isChatMuted) {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ToggleMuteOn");
            } else {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ToggleMuteOff");
            }
            String broadcastMessage = isChatMuted ?
                    langFile.getMessage("Messages.globalchat-muted").replace("%staffer%", staffer.getName()) :
                    langFile.getMessage("Messages.globalchat-unmuted").replace("%staffer%", staffer.getName());
            Bukkit.broadcastMessage(broadcastMessage);
            new MainStaffGui(langFile, settingsFile, staffer, page).open(staffer);
            return;
        }
        if (clickedSlot == getSafeSlot("Settings.MainGui.quickrtp-slot", 27)) {
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
            return;
        }
        if (slotToStafferMap.containsKey(clickedSlot)) {
            Player targetStaffer = slotToStafferMap.get(clickedSlot);
            if (targetStaffer == null || !targetStaffer.isOnline()) {
                SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
                staffer.sendMessage(langFile.getMessage("Messages.staffer-offline"));
                staffer.closeInventory();
                return;
            }

            ClickType click = event.getClick();
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.TeleportAction");
            if (click == ClickType.LEFT) {
                staffer.teleport(targetStaffer.getLocation());
                staffer.sendMessage(langFile.getMessage("Messages.teleported-to-staffer").replace("%target%", targetStaffer.getName()));
                staffer.closeInventory();
            } else if (click == ClickType.RIGHT) {
                targetStaffer.teleport(staffer.getLocation());
                staffer.sendMessage(langFile.getMessage("Messages.teleported-staffer-to-you").replace("%target%", targetStaffer.getName()));
                targetStaffer.sendMessage(langFile.getMessage("Messages.teleported-by-staffer").replace("%staffer%", staffer.getName()));
                staffer.closeInventory();
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
        int slot = getSafeSlot(configPath + "-slot", -1);
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
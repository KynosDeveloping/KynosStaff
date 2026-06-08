package it.kynos.KynosStaff.gui;

import it.kynos.KynosStaff.module.LangFile;
import it.kynos.KynosStaff.module.SettingsFile;
import it.kynos.kynoslib.menu.KynosGui;
import it.kynos.kynoslib.utils.SoundManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class TimeSelectionGui extends KynosGui {

    private final LangFile langFile;
    private final SettingsFile settingsFile;
    private final Player target;
    private final String configRootPath;
    private final Map<Integer, String> slotToTimeMap = new HashMap<>();

    public TimeSelectionGui(LangFile langFile, SettingsFile settingsFile, Player staffer, Player target, String configRootPath) {
        super(54, langFile.getMessage("GUI.TimeSelectionGui.title").replace("{target}", target.getName()));
        this.langFile = langFile;
        this.settingsFile = settingsFile;
        this.target = target;
        this.configRootPath = configRootPath;

        String glassMaterialName = settingsFile.getSetting("Settings.MainGui.filler-glass-material");
        Material glassMaterial;
        try {
            glassMaterial = Material.valueOf(glassMaterialName.toUpperCase());
        } catch (Exception e) {
            glassMaterial = Material.GRAY_STAINED_GLASS_PANE;
        }
        ItemStack glassPane = buildItem(glassMaterial, " ");
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                this.inventory.setItem(i, glassPane);
            }
        }

        ItemStack backItem = buildItem(Material.ARROW, langFile.getMessage("GUI.TimeSelectionGui.back-button.name"), langFile.getMessageList("GUI.TimeSelectionGui.back-button.lore"));
        this.inventory.setItem(45, backItem);

        for (int i = 1; i <= 5; i++) {
            String path = configRootPath + ".durations.option" + i;
            String timeValue = settingsFile.getSetting(path + ".time");
            String matName = settingsFile.getSetting(path + ".material");
            String slotStr = settingsFile.getSetting(path + ".slot");

            if (timeValue == null || matName == null || slotStr == null) continue;

            int slot;
            Material material;
            try {
                slot = Integer.parseInt(slotStr);
                material = Material.valueOf(matName.toUpperCase());
            } catch (Exception e) {
                continue;
            }

            String name = langFile.getMessage("GUI.TimeSelectionGui.item-format.name").replace("{time}", timeValue);
            this.inventory.setItem(slot, buildItem(material, name, langFile.getMessageList("GUI.TimeSelectionGui.item-format.lore")));
            slotToTimeMap.put(slot, timeValue);
        }
    }

    @Override
    public void handleSlotClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player staffer = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();

        if (clickedSlot == 45) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.DefaultClick");
            new PlayerManagementGui(langFile, settingsFile, staffer, target).open(staffer);
            return;
        }

        if (target == null || !target.isOnline()) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.ErrorAction");
            staffer.sendMessage(langFile.getMessage("Messages.player-offline"));
            staffer.closeInventory();
            return;
        }

        if (slotToTimeMap.containsKey(clickedSlot)) {
            SoundManager.playFromConfig(staffer, langFile.getConfig(), "GuiSounds.PunishAction");
            String timeSelected = slotToTimeMap.get(clickedSlot);
            String cmdTemplate = settingsFile.getSetting(configRootPath + ".command-template");

            if (cmdTemplate != null && !cmdTemplate.isEmpty()) {
                staffer.performCommand(cmdTemplate.replace("%target%", target.getName()).replace("%time%", timeSelected));
            }
            staffer.closeInventory();
        }
    }
}
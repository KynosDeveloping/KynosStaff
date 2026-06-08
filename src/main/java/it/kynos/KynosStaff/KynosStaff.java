package it.kynos.KynosStaff;

import it.kynos.KynosStaff.commands.*;
import it.kynos.KynosStaff.listeners.ChatListener;
import it.kynos.KynosStaff.listeners.PlayerJoin;
import it.kynos.KynosStaff.listeners.FreezeListener;
import it.kynos.KynosStaff.listeners.UpdateJoinListener;
import it.kynos.KynosStaff.module.LangFile;
import it.kynos.KynosStaff.module.SettingsFile;
import it.kynos.KynosStaff.utils.GitHubUpdater;
import it.kynos.kynoslib.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class KynosStaff extends JavaPlugin {

    private static KynosStaff instance;
    private LangFile langFile;
    private SettingsFile settingsFile;
    private MessageUtils msg;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.msg = new MessageUtils(this);
        this.langFile = new LangFile(this, this.msg);
        this.settingsFile = new SettingsFile(this);

        if (getCommand("staffpanel") != null) {
            getCommand("staffpanel").setExecutor(new MainStaffGuiCommand(this, "staffpanel"));
        }
        new GodmodeCommand(this, "godmode", langFile);
        new TrollStickCommand(this, "trollstick", langFile);
        new FlyCommand(this, "fly", langFile);
        new VanishCommand(this, "vanish", langFile);
        new FreezeCommand(this, "freeze", langFile);
        new MuteChatCommand(this, "mutechat", langFile);
        new KynosStaffCommand(this, "kynosstaff", langFile,settingsFile);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(langFile),this);
        getServer().getPluginManager().registerEvents(new UpdateJoinListener(this), this);

        new GitHubUpdater(this, "KynosDeveloping", "KynosStaff").checkForUpdate().thenAccept(latestVersion -> {
            if (latestVersion != null) {
                getLogger().warning("A new update (v" + latestVersion + ") is available on GitHub!");
            } else {
                getLogger().info("KynosStaff is up to date.");
            }
        });
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public SettingsFile getSettingsFile() {
        return settingsFile;
    }

    public LangFile getLangFile() {
        return langFile;
    }

    public static KynosStaff getInstance(){
        return instance;
    }

    public static boolean godmode(){
        boolean god = false;
        return god;
    }
}
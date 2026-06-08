package it.kynos.KynosStaff.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class GitHubUpdater {

    private final JavaPlugin plugin;
    private final String repoOwner;
    private final String repoName;

    public GitHubUpdater(JavaPlugin plugin, String repoOwner, String repoName) {
        this.plugin = plugin;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    public CompletableFuture<String> checkForUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            if (!plugin.getConfig().getBoolean("Settings.update-checker", true)) {
                return null;
            }

            try {
                URL url = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "KynosStaff-Updater");

                if (connection.getResponseCode() == 200) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String latestVersion = json.get("tag_name").getAsString().replace("v", "");
                    String currentVersion = plugin.getDescription().getVersion().replace("v", "");

                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        return latestVersion;
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates from GitHub: " + e.getMessage());
            }
            return null;
        });
    }
}
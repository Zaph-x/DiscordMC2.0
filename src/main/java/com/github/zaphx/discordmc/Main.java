package com.github.zaphx.discordmc;

import com.github.zaphx.discordmc.utilities.DiscordClientManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    // PLUGIN DESCRIPTION
    private String prefix = "§";
    private Logger log;
    private static Main main;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.log = this.getLogger();


        // Get the plugin manager
        PluginManager pm = Bukkit.getServer().getPluginManager();
        /* // DEPENDING ON ANOTHER PLUGIN?
        final Plugin PLUGIN_NAME = pm.getPlugin("PLUGIN");

        // Check for and enable PLUGIN_NAME
        if(PLUGIN_NAME != null && !PLUGIN_NAME.isEnabled()){
            getLogger().log(Level.WARNING, "DiscordMC2.0 could not find the core PLUGIN_NAME plugin!");
            getLogger().log(Level.WARNING, "DiscordMC2.0 has been disabled.");
            pm.disablePlugin(this);
            return;
        }
        */

        // Create configuration
        createConfig();
        this.config = this.getConfig();

        // Register the Main instance
        main = this;

        // Register events

        // Register commands

        // Register bot
        DiscordClientManager clientManager = DiscordClientManager.getInstance();
        if (!validateConfig()) {
            log.warning("The bot could not be started. Please fill in the config properly and try again.");
        } else {
        }

        getLogger().log(Level.INFO, "DiscordMC2.0 has successfully been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "DiscordMC2.0 has successfully been disabled!");
    }

    private void createConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            getLogger().log(Level.INFO, "No configuration found for DiscordMC2.0 " + getDescription().getVersion());
            saveDefaultConfig();
        } else {
            getLogger().log(Level.INFO, "Configuration found for DiscordMC2.0 v" + getDescription().getVersion() + "!");
        }
    }

    public static Main getInstance() {
        return main;
    }

    private boolean validateConfig() {
        if (config.getString("discord.token").isEmpty()) {
            log.warning("You must supply the plugin with a valid discord bot token");
            return false;
        }
        if (config.getLong("discord.guild-id") == 0) {
            log.warning("You must supply the plugin with a valid guild-id");
            return false;
        }
        if (config.getLong("discord.log-channel") == 0) {
            log.warning("You must supply the plugin with a valid log-channel");
            return false;
        }
        if (config.getLong("discord.rules-channel") == 0) {
            log.warning("You must supply the plugin with a valid rules-channel");
            return false;
        }
        if (config.getLong("discord.announce-channel") == 0) {
            log.warning("You must supply the plugin with a valid announce-channel");
            return false;
        }
        if (config.getLong("discord.mute-role") == 0) {
            log.warning("You must supply the plugin with a valid muted-role");
            return false;
        }
        if (config.getLong("discord.voice-mute-role") == 0) {
            log.warning("You must supply the plugin with a valid voice-muted-id");
            return false;
        }
        if (config.getLong("discord.reports-channel") == 0) {
            log.warning("You must supply the plugin with a valid reports-channel");
            return false;
        }
        if (config.getBoolean("trello.enabled")) {
            if (config.getString("trello.API-key").isEmpty()) {
                log.warning("You must supply the plugin with a valid trello API key");
                return false;
            }
            if (config.getString("trello.API-token").isEmpty()) {
                log.warning("You must supply the plugin with a valid trello API token");
                return false;
            }
            if (config.getString("trello.issues").isEmpty()) {
                log.warning("You must supply the plugin with a valid trello issues board");

            }
            if (config.getString("trello.suggestions").isEmpty()) {
                log.warning("You must supply the plugin with a valid trello suggestions board");

            }
        }
        if (config.getString("sql.host").isEmpty()) {
            log.warning("You must supply the plugin with a valid sql host address");
            return false;
        }
        if (config.getInt("sql.port") == 0) {
            log.warning("You must supply the plugin with a valid port for the sql server");
            return false;
        }
        if (config.getString("sql.username").isEmpty()) {
            log.warning("You must supply the plugin with a valid sql username");
            return false;
        }
        if (config.getString("sql.password").isEmpty()) {
            log.warning("You must supply the plugin with a valid sql password");
            return false;
        }
        if (config.getString("sql.database").isEmpty()) {
            log.warning("You must supply the plugin with a valid sql database");
            return false;
        }
        return true;

    }

    public String getPrefix() {
        return prefix;
    }
}
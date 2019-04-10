package com.github.zaphx.discordbot;

import com.github.zaphx.discordbot.discord.command.*;
import com.github.zaphx.discordbot.api.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.discord.listeners.*;
import com.github.zaphx.discordbot.managers.AntiSwearManager;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import com.github.zaphx.discordbot.minecraft.commands.MainCommand;
import com.github.zaphx.discordbot.minecraft.commands.ToDiscord;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.channel.TextChannelCreateEvent;
import discord4j.core.event.domain.channel.TextChannelDeleteEvent;
import discord4j.core.event.domain.guild.BanEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.event.domain.role.RoleCreateEvent;
import discord4j.core.event.domain.role.RoleDeleteEvent;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dizcord extends JavaPlugin {

    // PLUGIN DESCRIPTION
    private String prefix = "§";
    private Logger log;
    private static Dizcord dizcord;
    private FileConfiguration config;
    private DiscordClient client;
    private DiscordClientManager clientManager;

    @Override
    public void onEnable() {
        dizcord = this;
        this.log = this.getLogger();


        createConfig();
        saveDefaultConfig();
        this.config = dizcord.getConfig();
        clientManager = DiscordClientManager.getInstance();
        if (!validateConfig()) {
            log.warning("The bot could not be started. Please fill in the config properly and try again.");
        } else {

            log.info("Building client");
            client = clientManager.getClient();


        }
        log.info("Logging client in");
        clientManager.login(client);

        SQLManager sql = SQLManager.getInstance();

        getLogger().log(Level.INFO, "Registering listeners");
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> new OnReadyEvent().onReady());
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(event -> new UserJoinEvent().onUserJoinEvent(event));
        client.getEventDispatcher().on(MessageDeleteEvent.class).subscribe(event -> new ChatDeleteEvent().onMessageDelete(event));
        client.getEventDispatcher().on(BanEvent.class).subscribe(event -> new OnUserBanEvent().onUserBan(event));
        client.getEventDispatcher().on(MessageCreateEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new ChatListener().onChat(event));
        client.getEventDispatcher().on(TextChannelCreateEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new OnChannelCreateEvent().onChannelCreate(event));
        client.getEventDispatcher().on(TextChannelDeleteEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new OnChannelDeleteEvent().onChannelDelete(event));
        client.getEventDispatcher().on(MessageUpdateEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new OnChatEditEvent().onEdit(event));
        client.getEventDispatcher().on(RoleCreateEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new OnRoleCreateEvent().onCreateEvent(event));
        client.getEventDispatcher().on(RoleUpdateEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new OnRoleEditEvent().onEditEvent(event));
        client.getEventDispatcher().on(RoleDeleteEvent.class).onErrorContinue((t,o) -> {}).subscribe(event -> new OnRoleDeleteEvent().onDeleteEvent(event));

        sql.createMutesIfNotExists();
        sql.createAccountLinkIfNotExists();
        sql.createWarningsIfNotExists();
        sql.createMessagesIfNotExists();

        CommandHandler commandHandler = CommandHandler.getInstance();
        commandHandler.registerCommand("help", new Help());
        commandHandler.registerCommand("mute", new Mute());
        commandHandler.registerCommand("warn", new Warn());
        commandHandler.registerCommand("adallow", new AdAllow());
        commandHandler.registerCommand("mapmessages", new MapMessages());
        commandHandler.registerCommand("linkaccounts", new AccountLink());
        commandHandler.registerCommand("unlinkaccount", new AccountUnlink());
        commandHandler.registerCommand("whois", new WhoIs());
        commandHandler.registerCommand("events", new Event());

        getCommand("dizcord").setExecutor(new MainCommand());
        getCommand("todiscord").setExecutor(new ToDiscord());

        getLogger().log(Level.INFO, "Loading external bot plugins!");


        getLogger().log(Level.INFO, "Dizcord has successfully been enabled!" + (client.isConnected() ? " The client is also connected." : " The client is not connected."));
    }

    @Override
    public void onDisable() {
        clientManager.logout(client);
        getLogger().log(Level.INFO, "Dizcord has successfully been disabled!");
    }

    private void createConfig() {
        if (!getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
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

    public String getDiscordPrefix() {
        return this.getConfig().getString("discord.command-prefix");
    }

    /**
     * Returns the instance of the Dizcord bot
     *
     * @return The Dizcord instance
     */
    public static Dizcord getInstance() {
        return dizcord;
    }

    /**
     * This method will validate the plugin config.
     *
     * @return True if config is valid
     */
    private boolean validateConfig() {
        if (config.getString("discord.token").equalsIgnoreCase("")) {
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
        if (config.getLong("discord.reports-channel") == 0 && config.getBoolean("trello.enabled")) {
            log.warning("You must supply the plugin with a valid reports-channel");
            return false;
        }
        if (config.getLong("discord.suggestions-channel") == 0 && config.getBoolean("trello.enabled")) {
            log.warning("You must supply the plugin with a valid suggestions-channel");
            return false;
        }
        if (config.getBoolean("trello.enabled")) {
            if (config.getString("trello.API-key").equalsIgnoreCase("")) {
                log.warning("You must supply the plugin with a valid trello API key");
                return false;
            }
            if (config.getString("trello.API-token").equalsIgnoreCase("")) {
                log.warning("You must supply the plugin with a valid trello API token");
                return false;
            }
            if (config.getString("trello.issues").equalsIgnoreCase("")) {
                log.warning("You must supply the plugin with a valid trello issues board");

            }
            if (config.getString("trello.suggestions").equalsIgnoreCase("")) {
                log.warning("You must supply the plugin with a valid trello suggestions board");

            }
        }
        if (config.getString("sql.host").equalsIgnoreCase("")) {
            log.warning("You must supply the plugin with a valid sql host address");
            return false;
        }
        if (config.getInt("sql.port") == 0) {
            log.warning("You must supply the plugin with a valid port for the sql server");
            return false;
        }
        if (config.getString("sql.username").equalsIgnoreCase("")) {
            log.warning("You must supply the plugin with a valid sql username");
            return false;
        }
        if (config.getString("sql.database").equalsIgnoreCase("")) {
            log.warning("You must supply the plugin with a valid sql database");
            return false;
        }
        return true;

    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns an instance of the Dizcord logger
     *
     * @return The Dizcord logger
     */
    public Logger getLog() {
        return this.log;
    }
}
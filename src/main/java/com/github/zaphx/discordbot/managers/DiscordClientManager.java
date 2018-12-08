package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import org.bukkit.Bukkit;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DiscordClientManager {

    private IDiscordClient client;
    private static DiscordClientManager instance;

    /**
     * The token for the bot
     */
    private final String TOKEN = Dizcord.getInstance().getConfig().getString("discord.token");

    private DiscordClientManager() {
    }
    /**
     * Gets the instance of the DiscordClientManager
     * @return A new instance if one does not exist, else the instance
     */
    public static DiscordClientManager getInstance() {
        return instance == null ? instance = new DiscordClientManager() : instance;
    }

    /**
     * Logout the client
     *
     * @param client client to disconnect
     * @return True when disconnect was successful, False if otherwise
     */
    public boolean logout(IDiscordClient client) {
        try {
            Bukkit.getScheduler().cancelTasks(Dizcord.getInstance());

            client.logout();
            return true;
        } catch (DiscordException ignored) {
            return false;
        }
    }

    /**
     * Login the client
     *
     * @param client client to connect
     * @return True when connect was successful, False if otherwise
     */
    public boolean login(IDiscordClient client) {
        try {
            client.login();
            return true;
        } catch (DiscordException | RateLimitException ignored) {
            return false;
        }
    }

    /**
     * Checks if the client has a specific permission
     * @param permission The permission to check for
     * @return True if the client has permission, else false
     */
    public boolean clientHasPermission(Permissions permission) {
        IGuild guild = client.getGuildByID(Dizcord.getInstance().getConfig().getLong("discord.guild-id"));
        return getClient().getOurUser().getPermissionsForGuild(guild).contains(permission);
    }

    /**
     * Gets the client
     * @return The client
     */
    public IDiscordClient getClient() {
        return client == null ? client = new ClientBuilder().withToken(TOKEN).setMaxReconnectAttempts(200).build() : client;
    }
}

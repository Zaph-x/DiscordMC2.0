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

    private LocalDateTime uptime;
    public List<String> commandResponses = new ArrayList<>();
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
     * Sets the uptime of the bot
     * @param uptime The time of the startup
     */
    public void setUptime(LocalDateTime uptime) {
        this.uptime = uptime;
    }

    /**
     * Gets the current time
     * @return The current time
     */
    private LocalDateTime getTimeNow() {
        return LocalDateTime.now();
    }

    /**
     * Gets a string representation of the current uptime of the bot
     * @return The current uptime
     */
    public String getTotalUptime() {
        String uptime = "";
        long day, hour, minute;
        day = ChronoUnit.DAYS.between(this.uptime, getTimeNow());
        hour = ChronoUnit.HOURS.between(this.uptime, getTimeNow());
        minute = ChronoUnit.MINUTES.between(this.uptime, getTimeNow());
        if (day > 0) {
            if (day == 1) {
                uptime += day + " day, ";
            } else {
                uptime += day + " days, ";
            }
        }
        if ((hour % 24) > 0) {
            if (hour == 1) {
                uptime += (hour % 24) + " hour, and ";
            } else {
                uptime += (hour % 24) + " hours, and ";
            }
        }
        if ((minute % 60) == 1) {
            uptime += (minute % 60) + " minute.";
        } else if ((minute % 60) != 1) {
            uptime += (minute % 60) + " minutes.";
        }
        return uptime;
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
        return client == null ? client = new ClientBuilder().withToken(TOKEN).withRecommendedShardCount().setMaxReconnectAttempts(200).build() : client;
    }
}

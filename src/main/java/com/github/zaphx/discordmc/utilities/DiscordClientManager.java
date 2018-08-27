package com.github.zaphx.discordmc.utilities;

import com.github.zaphx.discordmc.Main;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
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

    private DiscordClientManager() {
    }

    public static DiscordClientManager getInstance() {
        return instance == null ? new DiscordClientManager() : instance;
    }

    /**
     * Logout the client
     *
     * @param client client to disconnect
     * @return True when disconnect was successful, False if otherwise
     */
    public boolean logout(IDiscordClient client) {
        try {
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

    public void setUptime(LocalDateTime uptime) {
        this.uptime = uptime;
    }

    private LocalDateTime getTimeNow() {
        return LocalDateTime.now();
    }

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

    public IDiscordClient getClient() {
        if (client == null) {
            String token = Main.getInstance().getConfig().getString("discord.token");
            client = new ClientBuilder().withToken(token).withRecommendedShardCount().setMaxReconnectAttempts(200).build();
        }
        return client;
    }

}

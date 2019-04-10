package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.ServiceMediator;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.Objects;

public class DiscordClientManager {

    private DiscordClient client;
    private static DiscordClientManager instance;
    public final long GUILD_Id = Dizcord.getInstance().getConfig().getLong("discord.guild-id");
    public final Snowflake GUILD_SNOWFLAKE = Snowflake.of(GUILD_Id);

    /**
     * The token for the bot
     */
    private final String TOKEN = Dizcord.getInstance().getConfig().getString("discord.token");

    private DiscordClientManager() {
    }

    /**
     * Gets the instance of the DiscordClientManager
     *
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
    public boolean logout(DiscordClient client) {
        Bukkit.getScheduler().cancelTasks(Dizcord.getInstance());

        client.logout().subscribe();
        return true;

    }

    /**
     * Login the client
     *
     * @param client client to connect
     * @return True when connect was successful, False if otherwise
     */
    public boolean login(DiscordClient client) {
        client.login().subscribe();
        return true;

    }

    /**
     * Checks if the client has a specific permission
     *
     * @param permission The permission to check for
     * @return True if the client has permission, else false
     */
    public boolean clientHasPermission(Permission permission) {
        Guild guild = client.getGuildById(Snowflake.of(GUILD_Id)).block();   // getGuildById(Dizcord.getInstance().getConfig().getLong("discord.guild-id"));
        Member ourUser = Objects.requireNonNull(guild).getMemberById(client.getSelfId().get()).block();
        return ourUser != null && Objects.requireNonNull(ourUser.getBasePermissions().block()).contains(permission); // getOurUser().getPermissionsForGuild(guild).contains(permission);
    }

    /**
     * Gets the client
     *
     * @return The client
     */
    public DiscordClient getClient() {
        return client == null ? client = new DiscordClientBuilder(TOKEN).build() : client;
    }

    public Member getSelf() {
        return client.getSelf().cast(Member.class).block();
    }

    public ServiceMediator getServiceMediator() {
        try {
            Field field = DiscordClient.class.getDeclaredField("serviceMediator");
            field.setAccessible(true);
            return (ServiceMediator) field.get(this.client);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

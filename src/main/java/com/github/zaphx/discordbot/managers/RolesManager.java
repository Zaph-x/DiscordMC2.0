package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Snowflake;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;

public class RolesManager {

    private static RolesManager instance;
    private TMap<String, Snowflake> roles = new THashMap<>();
    private Dizcord dizcord = Dizcord.getInstance();
    private FileConfiguration config = dizcord.getConfig();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private DiscordClient client = clientManager.getClient();

    private RolesManager() {
    }

    public static RolesManager getInstance() {
        return instance == null ? instance = new RolesManager() : instance;
    }

    public void mapRoles() {
        Guild guild = client.getGuildById(clientManager.GUILD_SNOWFLAKE).block();
        roles.clear();
        for (Role role : guild.getRoles().collectList().block()) {
            roles.put(role.getName(), role.getId());
        }
    }

    public Snowflake getRole(String name) {
        return client.getRoleById(clientManager.GUILD_SNOWFLAKE, roles.get(name)).block().getId();
    }

    public Role getRole(long Id) {
        return client.getRoleById(clientManager.GUILD_SNOWFLAKE, Snowflake.of(Id)).block();
    }

    public void addRole(Role role) {
        roles.put(role.getName(),role.getId());
    }

    public void removeRole(Role role) {
        roles.remove(role.getName());
    }

}

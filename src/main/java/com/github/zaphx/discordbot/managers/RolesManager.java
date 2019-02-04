package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

public class RolesManager {

    private static RolesManager instance;
    public TMap<String, Long> roles = new THashMap<>();
    private Dizcord dizcord = Dizcord.getInstance();
    private FileConfiguration config = dizcord.getConfig();
    private IDiscordClient client = DiscordClientManager.getInstance().getClient();

    private RolesManager() {
    }

    public static RolesManager getInstance() {
        return instance == null ? instance = new RolesManager() : instance;
    }

    public void mapRoles() {
        IGuild guild = client.getGuildByID(config.getLong("discord.guild-id"));
        roles.clear();
        for (IRole role : guild.getRoles()) {
            roles.put(role.getName(), role.getLongID());
        }
    }

    public IRole getRole(String name) {
        return client.getRoleByID(roles.get(name));
    }

    public IRole getRole(long ID) {
        return client.getRoleByID(ID);
    }

    public void addRole(IRole role) {
        roles.put(role.getName(),role.getLongID());
    }

    public void removeRole(IRole role) {
        roles.remove(role.getName());
    }

}

package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

public class RolesManager {

    /**
     * The instance of RoleManager
     */
    private static RolesManager instance;
    /**
     * The map of roles in the guild
     */
    public TMap<String, Long> roles = new THashMap<>();
    /**
     * The Discord instance
     */
    private Dizcord dizcord = Dizcord.getInstance();
    /**
     * The configuration file
     */
    private FileConfiguration config = dizcord.getConfig();
    /**
     * The client object
     */
    private IDiscordClient client = DiscordClientManager.getInstance().getClient();

    /**
     * The default constructor
     */
    private RolesManager() {
    }

    /**
     * Getter method for the instance of the RolesManager
     * @return The instance of the RoleManager
     */
    public static RolesManager getInstance() {
        return instance == null ? instance = new RolesManager() : instance;
    }

    /**
     * This method will map every role in the specified guild.
     */
    public void mapRoles() {
        IGuild guild = client.getGuildByID(config.getLong("discord.guild-id"));
        roles.clear();
        for (IRole role : guild.getRoles()) {
            roles.put(role.getName(), role.getLongID());
        }
    }

    /**
     * This method will get a role by its name
     * @param name The name of the role
     * @return A {@link IRole} from the name
     */
    public IRole getRole(String name) {
        return client.getRoleByID(roles.get(name));
    }

    /**
     * This method will get a role by its ID
     * @param ID The ID of the role
     * @return A {@link IRole} from the ID
     */
    public IRole getRole(long ID) {
        return client.getRoleByID(ID);
    }

    /**
     * This method will add a role to the role map
     * @param role The role to add
     */
    public void addRole(IRole role) {
        roles.put(role.getName(),role.getLongID());
    }

    /**
     * This method will remove a role from the role map
     * @param role The role to remove
     */
    public void removeRole(IRole role) {
        roles.remove(role.getName());
    }

}

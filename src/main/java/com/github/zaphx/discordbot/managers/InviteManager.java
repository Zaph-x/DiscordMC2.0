package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IExtendedInvite;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class InviteManager {

    /**
     * The instance of the InvitesManager
     */
    private static InviteManager instance;
    /**
     * A map of all invites for the guild
     */
    private Map<String, Integer> inviteMap = new TreeMap<>();
    /**
     * A map of every temporary invite
     */
    private Map<String, Integer> tempInviteMap = new TreeMap<>();
    /**
     * A map of all invite objects
     */
    private Map<String, IExtendedInvite> inviteObjectMap = new TreeMap<>();
    /**
     * The client manager
     */
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    /**
     * The discord client
     */
    private IDiscordClient client = clientManager.getClient();
    /**
     * The discord bot object
     */
    private Dizcord dizcord = Dizcord.getInstance();
    /**
     * The configuration
     */
    private FileConfiguration config = dizcord.getConfig();

    /**
     * The default constructor
     */
    private InviteManager() {
    }

    /**
     * Gets the instance of the InviteManager
     * @return A new instance if one does not exist, else the instance
     */
    public static InviteManager getInstance() {
        return instance == null ? instance = new InviteManager() : instance;
    }

    /**
     * Updates the stored guild invites
     */
    public void update() {
        List<IExtendedInvite> invites = client.getGuildByID(config.getLong("discord.guild-id")).getExtendedInvites();
        System.out.println("Found " + invites.size() + " invites. They are now registered");
        for (IExtendedInvite inv : invites) {
            inviteMap.put(inv.getCode(), inv.getUses());
            inviteObjectMap.put(inv.getCode(), inv);
        }
    }

    /**
     * Gets the change in invites
     * @return A newly created invite
     */
    private IExtendedInvite getInviteChange() {
        List<IExtendedInvite> invites = client.getGuildByID(config.getLong("discord.guild-id")).getExtendedInvites();
        if (invites.size() > inviteObjectMap.size()) {
            // new invite created
            for (IExtendedInvite invite : invites) {
                if (inviteObjectMap.get(invite.getCode()) == null) {
                    update();
                    return invite;
                }
            }
        }
        for (IExtendedInvite inv : invites) {
            tempInviteMap.put(inv.getCode(), inv.getUses());
            inviteObjectMap.put(inv.getCode(), inv);
        }
        for (Map.Entry<String, Integer> entry : inviteMap.entrySet()) {
            if (!tempInviteMap.get(entry.getKey()).equals(inviteMap.get(entry.getKey()))) {
                return inviteObjectMap.get(entry.getKey());
            }
        }
        Bukkit.getScheduler().runTaskLater(dizcord, this::getInviteChange, 40L);
        return null;
    }

    /**
     * Get who created an invite
     * @return User who created invite
     */
    public IUser getUserCreated() {
        return Objects.requireNonNull(getInviteChange()).getInviter();
    }

    /**
     * Gets an invite
     * @return An invite
     */
    public IExtendedInvite getInvite() {
        return getInviteChange();
    }
}

package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import discord4j.core.DiscordClient;
import discord4j.core.object.ExtendedInvite;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class InviteManager {

    private static InviteManager instance;
    private Map<String, Integer> inviteMap = new TreeMap<>();
    private Map<String, Integer> tempInviteMap = new TreeMap<>();
    private Map<String, ExtendedInvite> inviteObjectMap = new TreeMap<>();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private DiscordClient client = clientManager.getClient();
    private Dizcord dizcord = Dizcord.getInstance();
    private FileConfiguration config = dizcord.getConfig();

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
        List<ExtendedInvite> invites = client.getGuildById(Snowflake.of(clientManager.GUILD_Id)).block().getInvites().collectList().block();//getGuildById(config.getLong("discord.guild-id")).getExtendedInvites();
        System.out.println("Found " + invites.size() + " invites. They are now registered");
        for (ExtendedInvite inv : invites) {
            inviteMap.put(inv.getCode(), inv.getUses());
            inviteObjectMap.put(inv.getCode(), inv);
        }
    }

    /**
     * Gets the change in invites
     * @return A newly created invite
     */
    private ExtendedInvite getInviteChange() {
        List<ExtendedInvite> invites = client.getGuildById(Snowflake.of(clientManager.GUILD_Id))
                .block().getInvites().collectList().block(); //getGuildById(config.getLong("discord.guild-id")).getExtendedInvites();
        if (invites.size() > inviteObjectMap.size()) {
            // new invite created
            for (ExtendedInvite invite : invites) {
                if (inviteObjectMap.get(invite.getCode()) == null) {
                    update();
                    return invite;
                }
            }
        }
        for (ExtendedInvite inv : invites) {
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
    public Member getUserCreated() {
        return Objects.requireNonNull(getInviteChange()).getInviter().block().asMember(Snowflake.of(clientManager.GUILD_Id)).block();
    }

    /**
     * Gets an invite
     * @return An invite
     */
    public ExtendedInvite getInvite() {
        return getInviteChange();
    }
}

package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Main;
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

    private static InviteManager instance;
    private Map<String, Integer> inviteMap = new TreeMap<>();
    private Map<String, Integer> tempInviteMap = new TreeMap<>();
    private Map<String, IExtendedInvite> inviteObjectMap = new TreeMap<>();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private IDiscordClient client = clientManager.getClient();
    private Main main = Main.getInstance();
    private FileConfiguration config = main.getConfig();

    private InviteManager() {
        System.out.println("In");
    }

    public static InviteManager getInstance() {
        return instance == null ? instance = new InviteManager() : instance;
    }

    public void update() {
        List<IExtendedInvite> invites = client.getGuildByID(config.getLong("discord.guild-id")).getExtendedInvites();
        System.out.println(invites.size());
        for (IExtendedInvite inv : invites) {
            inviteMap.put(inv.getCode(), inv.getUses());
            inviteObjectMap.put(inv.getCode(), inv);
        }
    }

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
        Bukkit.getScheduler().runTaskLater(main, this::getInviteChange, 40L);
        return null;
    }

    public IUser getUserCreated() {
        return Objects.requireNonNull(getInviteChange()).getInviter();
    }

    public IExtendedInvite getInvite() {
        return getInviteChange();
    }
}

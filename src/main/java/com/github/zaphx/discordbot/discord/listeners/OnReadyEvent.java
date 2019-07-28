package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.*;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.util.logging.Level;

public class OnReadyEvent {

    /**
     * The message manager
     */
    private MessageManager messageManager = MessageManager.getInstance();
    /**
     * The invite manager
     */
    private InviteManager inviteManager = InviteManager.getInstance();
    /**
     * The roles manager
     */
    private RolesManager rolesManager = RolesManager.getInstance();
    /**
     * The SQL manager
     */
    private SQLManager sql = SQLManager.getInstance();
    /**
     * The channel manager
     */
    private ChannelManager channelManager = ChannelManager.getInstance();
    /**
     * The interval to use when checking for mutes
     */
    private long interval = Dizcord.getInstance().getConfig().getLong("discord.mute-check-interval",60);

    public OnReadyEvent() {
        Dizcord.getInstance().getLog().info("Registering " + getClass().getSimpleName());
    }

    /**
     * This event will handle when the bot is ready. This involves getting every message on the server, updating the invites, checking for mutes, mapping channels and mapping roles.
     * @param event
     */



    @EventSubscriber
    public void onReady(ReadyEvent event) {
        messageManager.setMessages();
        inviteManager.update();
        Dizcord.getInstance().getLogger().log(Level.INFO, "Checking for any mutes.");
        Bukkit.getScheduler().runTaskAsynchronously(Dizcord.getInstance(), () -> sql.unmute());
        Dizcord.getInstance().getLogger().log(Level.INFO, "Setting mute check interval to " + interval + " minutes");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Dizcord.getInstance(), () -> sql.unmute(), 20*60L * interval, 20*60L * interval);
        Dizcord.getInstance().getLog().info("Mapping current channels");
        channelManager.mapChannels();
        Dizcord.getInstance().getLog().info("Mapping current roles");
        rolesManager.mapRoles();
        Dizcord.getInstance().getLog().info("Ready");
    }
}

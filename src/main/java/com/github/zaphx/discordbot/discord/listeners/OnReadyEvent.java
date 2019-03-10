package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.*;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.bukkit.Bukkit;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

public class OnReadyEvent {

    private final MessageManager messageManager = MessageManager.getInstance();
    private final InviteManager inviteManager = InviteManager.getInstance();
    private final RolesManager rolesManager = RolesManager.getInstance();
    private final SQLManager sql = SQLManager.getInstance();
    private final ChannelManager channelManager = ChannelManager.getInstance();
    private final long interval = Dizcord.getInstance().getConfig().getLong("discord.mute-check-interval",60);



    public Mono<Void> onReady(final ReadyEvent event) {
        messageManager.setMessages();
        inviteManager.update();
        Dizcord.getInstance().getLogger().log(Level.INFO, "Checking for any mutes.");
        Bukkit.getScheduler().runTaskAsynchronously(Dizcord.getInstance(), sql::unmute);
        Dizcord.getInstance().getLogger().log(Level.INFO, "Setting mute check interval to " + interval + " minutes");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Dizcord.getInstance(), sql::unmute, 20*60L * interval, 20*60L * interval);
        Dizcord.getInstance().getLog().info("Mapping current channels");
        channelManager.mapChannels();
        Dizcord.getInstance().getLog().info("Mapping current roles");
        rolesManager.mapRoles();
        Dizcord.getInstance().getLog().info("Ready");
    }
}

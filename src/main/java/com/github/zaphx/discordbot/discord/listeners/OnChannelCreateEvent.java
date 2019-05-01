package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.ChannelManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;

public class OnChannelCreateEvent {

    /**
     * The channel manager
     */
    private ChannelManager channelManager = ChannelManager.getInstance();

    /**
     * This event will handle when a channel is created
     * @param event The event to handle
     */
    @EventSubscriber
    public void onChannelCreate(ChannelCreateEvent event) {
        channelManager.addChannel(event);
    }

}

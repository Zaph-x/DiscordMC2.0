package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.ChannelManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;

public class OnChannelDeleteEvent {

    /**
     * The channel manager
     */
    private ChannelManager channelManager = ChannelManager.getInstance();

    /**
     * This event will handle when a channel is deleted
     * @param event The event to handle
     */
    @EventSubscriber
    public void onChannelDelete(ChannelDeleteEvent event) {
        channelManager.removeChannel(event);

    }

}

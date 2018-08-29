package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.utilities.ChannelManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;

public class OnChannelDeleteEvent {

    private ChannelManager channelManager = ChannelManager.getInstance();

    @EventSubscriber
    public void onChannelDelete(ChannelDeleteEvent event) {
        channelManager.removeChannel(event);

    }

}

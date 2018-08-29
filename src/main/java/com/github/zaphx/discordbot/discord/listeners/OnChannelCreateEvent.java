package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.ChannelManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;

public class OnChannelCreateEvent {

    private ChannelManager channelManager = ChannelManager.getInstance();

    @EventSubscriber
    public void onChannelCreate(ChannelCreateEvent event) {
        channelManager.addChannel(event);
    }

}

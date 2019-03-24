package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.ChannelManager;
import discord4j.core.event.domain.channel.TextChannelCreateEvent;
import reactor.core.publisher.Mono;

public class OnChannelCreateEvent {

    private ChannelManager channelManager = ChannelManager.getInstance();

    public void onChannelCreate(TextChannelCreateEvent event) {
        channelManager.addChannel(event);
    }

}

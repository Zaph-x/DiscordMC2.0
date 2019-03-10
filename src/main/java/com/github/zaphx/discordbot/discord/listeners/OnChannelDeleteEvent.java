package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.ChannelManager;
import discord4j.core.event.domain.channel.TextChannelDeleteEvent;

public class OnChannelDeleteEvent {

    private ChannelManager channelManager = ChannelManager.getInstance();

    public void onChannelDelete(TextChannelDeleteEvent event) {

        channelManager.removeChannel(event);

    }

}

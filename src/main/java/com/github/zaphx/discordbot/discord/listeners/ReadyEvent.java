package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.trello.TrelloManager;
import com.github.zaphx.discordbot.utilities.ChannelManager;
import com.github.zaphx.discordbot.utilities.EmbedManager;
import com.github.zaphx.discordbot.utilities.InviteManager;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;

public class ReadyEvent extends Event {

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        TrelloManager trelloManager = TrelloManager.getInstance();
        InviteManager inviteManager = InviteManager.getInstance();
        ChannelManager channelManager = ChannelManager.getInstance();
        EmbedManager embedManager = EmbedManager.getInstance();
    }

}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.SQLManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent;

public class OnChatEditEvent {

    private SQLManager sqlManager = SQLManager.getInstance();

    @EventSubscriber
    public void onEdit(MessageEditEvent e) {
        sqlManager.executeStatementAndPost("UPDATE " + sqlManager.prefix + "messages SET content = '%s' WHERE id = '%s'", e.getMessage().getContent(), e.getMessage().getStringID());
    }

}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.SQLManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent;

public class OnChatEditEvent {

    /**
     * The SQL manager
     */
    private SQLManager sqlManager = SQLManager.getInstance();

    /**
     * This event will handle when a message is edited
     * @param e The event to handle
     */
    @EventSubscriber
    public void onEdit(MessageEditEvent e) {
        sqlManager.executeStatementAndPost("UPDATE " + sqlManager.prefix + "messages SET content = '%s' WHERE id = '%s'", e.getMessage().getContent().replaceAll("'", "¼"), e.getMessage().getStringID());
    }

}

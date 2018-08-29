package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.discord.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.trello.TrelloManager;
import com.github.zaphx.discordbot.trello.TrelloType;
import com.github.zaphx.discordbot.managers.ChannelManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class ChatListener {

    private ChannelManager channelManager = ChannelManager.getInstance();
    private TrelloManager trelloManager = TrelloManager.getInstance();
    private CommandHandler commandHandler = CommandHandler.getInstance();

    @EventSubscriber
    public void onChat(MessageReceivedEvent event) {

        trelloManager.checkAndSend(event, TrelloType.ISSUE);
        trelloManager.checkAndSend(event, TrelloType.SUGGESTION);
        commandHandler.checkForCommand(event);
    }

}

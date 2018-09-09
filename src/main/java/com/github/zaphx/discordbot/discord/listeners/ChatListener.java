package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.discord.AntiAdvertisement;
import com.github.zaphx.discordbot.api.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.trello.TrelloManager;
import com.github.zaphx.discordbot.trello.TrelloType;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class ChatListener {

    private MessageManager messageManager = MessageManager.getInstance();
    private TrelloManager trelloManager = TrelloManager.getInstance();
    private CommandHandler commandHandler = CommandHandler.getInstance();

    @EventSubscriber
    public void onChat(MessageReceivedEvent event) {
        new AntiAdvertisement().checkAndHandle(event);
        trelloManager.checkAndSend(event, TrelloType.ISSUE);
        trelloManager.checkAndSend(event, TrelloType.SUGGESTION);
        commandHandler.checkForCommand(event);
        messageManager.addMessage(event.getMessage());
    }

}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.discord.AntiAdvertisement;
import com.github.zaphx.discordbot.api.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.managers.AntiSwearManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.trello.TrelloManager;
import com.github.zaphx.discordbot.trello.TrelloType;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class ChatListener {

    private MessageManager messageManager = MessageManager.getInstance();
    private TrelloManager trelloManager = TrelloManager.getInstance();
    private CommandHandler commandHandler = CommandHandler.getInstance();
    private AntiSwearManager antiSwearManager = AntiSwearManager.getInstance();

    public void onChat(final MessageCreateEvent event) {
        new AntiAdvertisement().checkAndHandle(event);
        antiSwearManager.handleMessage(event.getMessage());
        trelloManager.checkAndSend(event, TrelloType.ISSUE);
        trelloManager.checkAndSend(event, TrelloType.SUGGESTION);
        commandHandler.checkForCommand(event);
        messageManager.addMessage(event.getMessage());
    }

}

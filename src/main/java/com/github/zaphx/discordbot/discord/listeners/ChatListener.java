package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.discord.AntiAdvertisement;
import com.github.zaphx.discordbot.api.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.managers.AntiSwearManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.trello.TrelloManager;
import com.github.zaphx.discordbot.trello.TrelloType;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class ChatListener {

    /**
     * The message manager
     */
    private MessageManager messageManager = MessageManager.getInstance();
    /**
     * The trello manager
     */
    private TrelloManager trelloManager = TrelloManager.getInstance();
    /**
     * The command handler
     */
    private CommandHandler commandHandler = CommandHandler.getInstance();
    /**
     * The anti swear manager
     */
    private AntiSwearManager antiSwearManager = AntiSwearManager.getInstance();

    public ChatListener() {
        Dizcord.getInstance().getLog().info("Registering " + getClass().getSimpleName());
    }

    /**
     * This event will handle when a message is received. If the message is an issue or a suggestion, it will be sent to the trello board (If enabled). This will also handle commands, swears and advertisements
     *
     * @param event The event to handle
     */
    @EventSubscriber
    public void onChat(MessageReceivedEvent event) {
        new AntiAdvertisement().checkAndHandle(event);
        antiSwearManager.handleMessage(event.getMessage());
        trelloManager.checkAndSend(event, TrelloType.ISSUE);
        trelloManager.checkAndSend(event, TrelloType.SUGGESTION);
        commandHandler.checkForCommand(event);
        messageManager.addMessage(event.getMessage());
    }

}

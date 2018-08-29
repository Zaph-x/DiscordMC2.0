package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.InviteManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class OnReadyEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        messageManager.setMessages();
        inviteManager.update();
    }
}

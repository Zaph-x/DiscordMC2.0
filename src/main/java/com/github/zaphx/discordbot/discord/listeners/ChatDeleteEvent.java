package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.utilities.EmbedManager;
import com.github.zaphx.discordbot.utilities.MessageManager;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.obj.IMessage;

public class ChatDeleteEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private EmbedManager em = EmbedManager.getInstance();

    @EventSubscriber
    public void onMessageDelete(MessageDeleteEvent event) {
        long id = event.getMessageID();
        IMessage content = messageManager.getMessageFromLog(id);
        if (content.getAuthor().isBot()) {
            return;
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                messageManager.auditlog(em.messageDeleteEmbed(content.getAuthor(), event.getChannel(), content));
                messageManager.destroyMessages();
                messageManager.setMessages();
            }, 40L);
        }
    }



}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import gnu.trove.map.hash.THashMap;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.obj.IMessage;

public class ChatDeleteEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private EmbedManager em = EmbedManager.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();

    @EventSubscriber
    public void onMessageDelete(MessageDeleteEvent event) {
        long id = event.getMessageID();
        THashMap<String, String> map = messageManager.getDeletedMessage("" + id);
        if ((map.get("content")).toLowerCase().startsWith("ob!")) return;
        if (!clientManager.getClient().getUserByID(Long.parseLong(map.get("author"))).isBot()) {
            messageManager.auditlog(em.messageDeleteEmbed(map));
        }
    }
}

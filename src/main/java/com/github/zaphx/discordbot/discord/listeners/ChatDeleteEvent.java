package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import gnu.trove.map.hash.THashMap;

import java.util.Objects;

public class ChatDeleteEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private EmbedManager em = EmbedManager.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();

    public void onMessageDelete(MessageDeleteEvent event) {
        long id = event.getMessageId().asLong();
        THashMap<String, String> map = messageManager.getDeletedMessage("" + id);
        if ((map.get("content")).toLowerCase().startsWith("ob!")) return;
        if (!Objects.requireNonNull(clientManager.getClient().getMemberById(Snowflake.of(clientManager.GUILD_ID), event.getMessage().get().getAuthor().get().getId()).block()).isBot()) {
            messageManager.auditlog(em.messageDeleteEmbed(map));
        }
    }
}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.util.Snowflake;
import gnu.trove.map.hash.THashMap;

public class ChatDeleteEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private EmbedManager em = EmbedManager.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();

    public void onMessageDelete(final MessageDeleteEvent event) {
        THashMap<String, String> map = messageManager.getDeletedMessage(event.getMessageId().asString());
        if (map.get("content").toLowerCase().startsWith("ob!")) return;
        clientManager.getClient().getMemberById(
                clientManager.GUILD_SNOWFLAKE,
                Snowflake.of(map.get("author")))
                .subscribe(member -> {
                    if (member.isBot()) messageManager.auditlog(em.messageDeleteEmbed(map));
                });
    }
}

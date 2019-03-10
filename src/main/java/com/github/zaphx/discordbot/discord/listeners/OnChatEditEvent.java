package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.SQLManager;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class OnChatEditEvent {

    private SQLManager sqlManager = SQLManager.getInstance();

    public void onEdit(final MessageUpdateEvent event) {
        Mono.justOrEmpty(event.getMessage()).subscribe(this::updateSQLEntry);
    }

    private void updateSQLEntry(Mono<Message> message) {
        Message m = message.block();
        if (m != null) {
            sqlManager.executeStatementAndPost("UPDATE " + sqlManager.prefix + "messages SET content = '%s' WHERE id = '%s'",
                    m.getContent().orElse("STRING CONTENT NOT FOUND IN MESSAGE").replaceAll("'", "¼"),
                    m.getId().asString());
        }
    }
}

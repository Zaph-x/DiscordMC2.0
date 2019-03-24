package com.github.zaphx.discordbot.trello;

import com.github.zaphx.discordbot.utilities.ArgumentException;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;

import java.util.ArrayList;
import java.util.List;

public class TrelloEventBuilder {

    private TrelloManager trelloManager = TrelloManager.getInstance();

    private List<String> attachments = new ArrayList<>();

    private boolean isValidReport = true;
    private MessageCreateEvent event;
    private TrelloType trelloType;
    private Message message;
    private TextChannel channel;
    private User sender;

    TrelloEventBuilder(MessageCreateEvent event) {
        this.event = event;
        this.message = event.getMessage();
        this.channel = (TextChannel) event.getMessage().getChannel().block();
        this.sender = event.getMember().orElseThrow(ArgumentException::new);
    }

    TrelloEventBuilder setType(TrelloType type) {
        this.trelloType = type;
        return this;
    }

    public TrelloEventBuilder checkChannel(DiscordChannelTypes channelType) {
        if (this.channel.getId().asLong() != channelType.getId()) {
            this.isValidReport = false;
        }
        return this;
    }

    TrelloEventBuilder addAttachments() {
        if (this.message.getAttachments().size() > 0) {
            this.message.getAttachments().forEach(attachment -> {
                attachments.add("![Attachment](" + attachment.getUrl() + ")");
            });
        }
        return this;
    }

    void build() {
        if (!isValidReport) {
            System.out.println(false);
            return;
        }
        isValidReport = trelloManager.checkValidity(this);
        if (!isValidReport) {
            System.out.println(false);
            return;
        }
        System.out.println(true);
        trelloManager.officiallyFileReport(this, this.trelloType);
    }

    Message getMessage() {
        return message;
    }

    MessageCreateEvent getEvent() {
        return event;
    }

    TrelloType getType() {
        return trelloType;
    }

    TextChannel getChannel() {
        return channel;
    }

    boolean getValidity() {
        return isValidReport;
    }

    List<String> getAttachments() {
        return attachments;
    }

    User getSender() {
        return sender;
    }
}

package com.github.zaphx.discordbot.trello;

import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.List;

public class TrelloEventBuilder {

    private TrelloManager trelloManager = TrelloManager.getInstance();

    private List<String> attachments = new ArrayList<>();

    private boolean isValidReport = true;
    private MessageReceivedEvent event;
    private TrelloType trelloType;
    private IMessage message;
    private IChannel channel;

    public TrelloEventBuilder(MessageReceivedEvent event) {
        this.event = event;
        this.message = event.getMessage();
        this.channel = event.getChannel();
    }

    public TrelloEventBuilder setType(TrelloType type) {
        this.trelloType = type;
        return this;
    }

    public TrelloEventBuilder checkChannel(DiscordChannelTypes channelType) {
        if (this.channel != channelType.getChannel()) {
            this.isValidReport = false;
        }
        return this;
    }

    public TrelloEventBuilder addAttachments() {
        if (this.message.getAttachments().size() > 0) {
            this.message.getAttachments().forEach(attachment -> {
                attachments.add("![Attachment](" + attachment.getUrl() + ")");
            });
        }
        return this;
    }

    public void build() {
        if (!isValidReport) {
            return;
        }
        isValidReport = trelloManager.checkValidity(this);
        if (!isValidReport) {
            return;
        }
        trelloManager.officiallyFileReport(this, this.trelloType);
    }

    public IMessage getMessage() {
        return message;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public TrelloType getType() {
        return trelloType;
    }

    public IChannel getChannel() {
        return channel;
    }

    public boolean getValidity() {
        return isValidReport;
    }

    public List<String> getAttachments() {
        return attachments;
    }
}

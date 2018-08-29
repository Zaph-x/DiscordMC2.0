package com.github.zaphx.discordbot.trello;

import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

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
    private IUser sender;

    public TrelloEventBuilder(MessageReceivedEvent event) {
        this.event = event;
        this.message = event.getMessage();
        this.channel = event.getChannel();
        this.sender = event.getAuthor();
    }

    public TrelloEventBuilder setType(TrelloType type) {
        this.trelloType = type;
        return this;
    }

    public TrelloEventBuilder checkChannel(DiscordChannelTypes channelType) {
        if (this.channel.getLongID() != channelType.getID()) {
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

    IMessage getMessage() {
        return message;
    }

    MessageReceivedEvent getEvent() {
        return event;
    }

    TrelloType getType() {
        return trelloType;
    }

    IChannel getChannel() {
        return channel;
    }

    boolean getValidity() {
        return isValidReport;
    }

    List<String> getAttachments() {
        return attachments;
    }

    IUser getSender() {
        return sender;
    }
}

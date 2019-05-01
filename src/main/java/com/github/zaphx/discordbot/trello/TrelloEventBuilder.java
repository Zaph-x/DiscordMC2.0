package com.github.zaphx.discordbot.trello;

import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

public class TrelloEventBuilder {

    /**
     * The trello manager instance
     */
    private TrelloManager trelloManager = TrelloManager.getInstance();
    /**
     * The attachments to send to trello
     */
    private List<String> attachments = new ArrayList<>();
    /**
     * Checker if the report is valid
     */
    private boolean isValidReport = true;
    /**
     * The message recieved event to look in
     */
    private MessageReceivedEvent event;
    /**
     * The type of trello event we are handling
     */
    private TrelloType trelloType;
    /**
     * The message to parse
     */
    private IMessage message;
    /**
     * The channel the message was sent in
     */
    private IChannel channel;
    /**
     * The sender of the report or suggestion
     */
    private IUser sender;

    /**
     * The consstructor for the builder
     * @param event The event to look in
     */
    TrelloEventBuilder(MessageReceivedEvent event) {
        this.event = event;
        this.message = event.getMessage();
        this.channel = event.getChannel();
        this.sender = event.getAuthor();
    }

    /**
     * This method sets the type of trello event we are handling
     * @param type The type of trello event
     * @return The builder
     */
    TrelloEventBuilder setType(TrelloType type) {
        this.trelloType = type;
        return this;
    }

    /**
     * This mehtod will check if the channel is a valid channel
     * @param channelType The channel type to look for
     * @return The builder
     */
    public TrelloEventBuilder checkChannel(DiscordChannelTypes channelType) {
        if (this.channel.getLongID() != channelType.getID()) {
            this.isValidReport = false;
        }
        return this;
    }

    /**
     * This method adds an attachment to the builder
     * @return The builder
     */
    TrelloEventBuilder addAttachments() {
        if (this.message.getAttachments().size() > 0) {
            this.message.getAttachments().forEach(attachment -> {
                attachments.add("![Attachment](" + attachment.getUrl() + ")");
            });
        }
        return this;
    }

    /**
     * This method will build the event and handle it. This will also file the report or suggestion to trello
     */
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

    /**
     * Gets the message sent
     * @return The message we're handling
     */
    IMessage getMessage() {
        return message;
    }

    /**
     * This method will get the event we're handling
     * @return The event being handled
     */
    MessageReceivedEvent getEvent() {
        return event;
    }

    /**
     * This method will get the type of trello event we're dealing with
     * @return The event type
     */
    TrelloType getType() {
        return trelloType;
    }

    /**
     * This method will get the channel the message was sent in
     * @return the channel we're dealing with
     */
    IChannel getChannel() {
        return channel;
    }

    /**
     * This method will get if the report or suggestion was formatted correctly
     * @return
     */
    boolean getValidity() {
        return isValidReport;
    }

    /**
     * This method will get the attachments if any
     * @return The attachment list
     */
    List<String> getAttachments() {
        return attachments;
    }

    /**
     * This method will get whoever filed the report or suggestion
     * @return the user sending the message
     */
    IUser getSender() {
        return sender;
    }
}

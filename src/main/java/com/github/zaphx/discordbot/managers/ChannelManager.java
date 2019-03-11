package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.channel.TextChannelCreateEvent;
import discord4j.core.event.domain.channel.TextChannelDeleteEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.json.request.EmbedRequest;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.soap.Text;
import java.util.Objects;

public class ChannelManager {

    private static ChannelManager instance;
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private DiscordClient client = clientManager.getClient();
    private TMap<String, Long> channelMap = new THashMap<>();
    private Dizcord dizcord = Dizcord.getInstance();
    private FileConfiguration config = dizcord.getConfig();

    private ChannelManager() {
    }

    /**
     * Gets the instance of the ChannelManager
     *
     * @return A new instance if one does not exist, else the instance
     */
    public static ChannelManager getInstance() {
        return instance == null ? instance = new ChannelManager() : instance;
    }

    /**
     * This method maps all available channels to a map.
     *
     * @return The TreeMap of channels.
     */
    public void mapChannels() {
        client.getGuildById(Snowflake.of(clientManager.GUILD_ID)).subscribe(guild -> {
            channelMap.clear();
            guild.getChannels()
                    .filter(guildChannel -> guildChannel instanceof TextChannel)
                    .map(c -> channelMap.put(c.getName(), c.getId().asLong()));
        });
    }

    /**
     * Adds a new channel to the THashMap channels.
     *
     * @param event The event to get the channel from.
     */
    public void addChannel(TextChannelCreateEvent event) {
        TextChannel channel = event.getChannel();
        channelMap.put(channel.getName(), channel.getId().asLong());
    }

    /**
     * Removes a channel from the THashMap channels.
     *
     * @param event The event to get the channel from.
     */
    public void removeChannel(TextChannelDeleteEvent event) {
        channelMap.remove(event.getChannel().getName());
    }

    /**
     * This method gets a channel by name, from the THashMap channels.
     *
     * @param name The name to look for
     * @return The channel from name if it exists. Else null
     */
    public TextChannel getChannel(String name) {
        return channelMap.get(name) != null ? client.getChannelById(Snowflake.of(channelMap.get(name))).cast(TextChannel.class).block() : null;
    }

    /**
     * This method gets a channel by its ID, from the THashMap channels.
     *
     * @param ID The ID of the channel to look for
     * @return The channel from the ID provided.
     */
    public TextChannel getChannel(long ID) {
        return client.getChannelById(Snowflake.of(ID)).cast(TextChannel.class).block();
    }

    /**
     * This method gets a channel by type, from the THashMap channels.
     *
     * @param types The DiscordChannelTypes type
     * @return The channel from name if it exists. Else null
     */
    public TextChannel getChannel(DiscordChannelTypes types) {
        return client.getChannelById(Snowflake.of(types.getID())).cast(TextChannel.class).block();
    }

    /**
     * This method checks if the ID provided is the channel also provided
     *
     * @param channel The channel to check
     * @param ID      The ID to check
     * @return True if ID and channel match
     */
    public boolean isChannel(TextChannel channel, long ID) {
        return channel.getId().asLong() == ID;
    }

    /**
     * This method checks if the ID provided is the channel also provided
     *
     * @param snowflake The snowflake to check
     * @param ID      The ID to check
     * @return True if ID and channel match
     */
    public boolean isChannel(Snowflake snowflake, long ID) {
        return snowflake.asLong() == ID;
    }

    /**
     * This method checks if the two channels provided are the same
     *
     * @param channel The channel to check
     * @param target  The other channel to check
     * @return True if channels match
     */
    public boolean isChannel(TextChannel target, TextChannel channel) {
        return target == channel || Objects.equals(target, channel);
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param event   The provided MessageEvent
     * @param message The message to send
     */
    public void sendMessageToChannel(MessageCreateEvent event, String message) {
        Mono.justOrEmpty(event).flatMap(e -> e.getMessage().getChannel().map(messageChannel -> messageChannel.createMessage(message))).subscribe();
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param event   The provided MessageEvent
     * @param message The message to send
     */
    public void sendMessageToChannel(MessageCreateEvent event, EmbedRequest message) {
        event.getMessage().getChannel().map(messageChannel -> messageChannel.createMessage(message.toString())).subscribe();
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param event   The provided MessageEvent
     * @param message The message to send
     */
    public void sendMessageToChannel(MessageDeleteEvent event, String message) {
        Mono.justOrEmpty(event).flatMap(e -> e.getChannel().map(messageChannel -> messageChannel.createMessage(message))).subscribe();
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param event   The provided MessageEvent
     * @param message The message to send
     */
    public void sendMessageToChannel(MessageDeleteEvent event, EmbedRequest message) {
        Mono.justOrEmpty(event).flatMap(e -> e.getChannel().map(messageChannel -> messageChannel.createMessage(message.toString()))).subscribe();
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(Channel channel, MessageCreateSpec message) {

        Mono.justOrEmpty(channel).filter(c -> c instanceof TextChannel)
                .cast(TextChannel.class)
                .map(c -> c.createMessage(new MessageCreateSpec().setEmbed(message)));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IChannel channel, String message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(DiscordChannelTypes channel, EmbedObject message) {
        RequestBuffer.request(() -> channel.getChannel().sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(DiscordChannelTypes channel, String message) {
        RequestBuffer.request(() -> channel.getChannel().sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param id      The id of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(long id, EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(id).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param id      The id of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(long id, String message) {
        RequestBuffer.request(() -> client.getChannelByID(id).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param name    The name of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(String name, EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(channelMap.get(name)).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param name    The name of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(String name, String message) {
        RequestBuffer.request(() -> client.getChannelByID(channelMap.get(name)).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param channel The private channel you are sending a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IPrivateChannel channel, EmbedObject message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     *
     * @param channel The private channel you are sending a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IPrivateChannel channel, String message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

}

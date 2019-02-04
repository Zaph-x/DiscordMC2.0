package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.RequestBuffer;

public class ChannelManager {

    private static ChannelManager instance;
    private IDiscordClient client = DiscordClientManager.getInstance().getClient();
    private TMap<String, Long> channelMap = new THashMap<>();
    private Dizcord dizcord = Dizcord.getInstance();
    private FileConfiguration config = dizcord.getConfig();

    private ChannelManager() {
    }

    /**
     * Gets the instance of the ChannelManager
     * @return A new instance if one does not exist, else the instance
     */
    public static ChannelManager getInstance() {
        return instance == null ? instance = new ChannelManager() : instance;
    }

    /**
     * This method maps all available channels to a map.
     * @return The TreeMap of channels.
     */
    public void mapChannels() {
        IGuild guild = client.getGuildByID(config.getLong("discord.guild-id"));
        channelMap.clear();
        for (IChannel channel : guild.getChannels()) {
            channelMap.put(channel.getName(), channel.getLongID());
        }
    }

    /**
     * Adds a new channel to the THashMap channels.
     * @param e The event to get the channel from.
     */
    public void addChannel(ChannelCreateEvent e) {
        IChannel channel = e.getChannel();
        channelMap.put(channel.getName(),channel.getLongID());
    }

    /**
     * Removes a channel from the THashMap channels.
     * @param e The event to get the channel from.
     */
    public void removeChannel(ChannelDeleteEvent e) {
        channelMap.remove(e.getChannel().getName());
    }

    /**
     * This method gets a channel by name, from the THashMap channels.
     * @param name The name to look for
     * @return The channel from name if it exists. Else null
     */
    public IChannel getChannel(String name) {
        return channelMap.get(name) != null ? client.getChannelByID(channelMap.get(name)) : null;
    }

    /**
     * This method gets a channel by its ID, from the THashMap channels.
     * @param ID The ID of the channel to look for
     * @return The channel from the ID provided.
     */
    public IChannel getChannel(long ID) {
        return client.getChannelByID(ID);
    }

    /**
     * This method gets a channel by type, from the THashMap channels.
     * @param types The DiscordChannelTypes type
     * @return The channel from name if it exists. Else null
     */
    public IChannel getChannel(DiscordChannelTypes types) {
        return client.getChannelByID(types.getID());
    }

    /**
     * This method checks if the ID provided is the channel also provided
     * @param channel The channel to check
     * @param ID The ID to check
     * @return True if ID and channel match
     */
    public boolean isChannel(IChannel channel, long ID) {
        return channel.getLongID() == ID;
    }

    /**
     * This method checks if the two channels provided are the same
     * @param channel The channel to check
     * @param target The other channel to check
     * @return True if channels match
     */
    public boolean isChannel(IChannel target, IChannel channel) {
        return target == channel;
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param event The provided MessageEvent
     * @param message The message to send
     */
    public void sendMessageToChannel(MessageEvent event, String message) {
        RequestBuffer.request(() -> event.getChannel().sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param event The provided MessageEvent
     * @param message The message to send
     */
    public void sendMessageToChannel(MessageEvent event, EmbedObject message) {
        RequestBuffer.request(() -> event.getChannel().sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IChannel channel, EmbedObject message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IChannel channel, String message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(DiscordChannelTypes channel, EmbedObject message) {
        RequestBuffer.request(() -> channel.getChannel().sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param channel The channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(DiscordChannelTypes channel, String message) {
        RequestBuffer.request(() -> channel.getChannel().sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param id The id of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(long id, EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(id).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param id The id of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(long id, String message) {
        RequestBuffer.request(() -> client.getChannelByID(id).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param name The name of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(String name, EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(channelMap.get(name)).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param name The name of the channel to send a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(String name, String message) {
        RequestBuffer.request(() -> client.getChannelByID(channelMap.get(name)).sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param channel The private channel you are sending a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IPrivateChannel channel, EmbedObject message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

    /**
     * Sends a message to a channel provided by the MessageEvent
     * @param channel The private channel you are sending a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IPrivateChannel channel, String message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

}

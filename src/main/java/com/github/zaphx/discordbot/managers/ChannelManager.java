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

    public static ChannelManager getInstance() {
        return instance == null ? instance = new ChannelManager() : instance;
    }

    /**
     * This method maps all available channels to a map.
     * @return The TreeMap of channels.
     */
    public TMap<String, Long> mapChannels() {
        IGuild guild = client.getGuildByID(config.getLong("discord.guild-id"));
        channelMap.clear();
        for (IChannel channel : guild.getChannels()) {
            channelMap.put(channel.getName(), channel.getLongID());
        }
        return channelMap;
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
     * This method gets a channel by type, from the THashMap channels.
     * @param types The DiscordChannelTypes type
     * @return The channel from name if it exists. Else null
     */
    public IChannel getChannel(DiscordChannelTypes types) {
        return client.getChannelByID(types.getID());
    }

    public boolean isChannel(IChannel channel, long ID) {
        return channel.getLongID() == ID;
    }

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
    public void sendMessageToChannel(DiscordChannelTypes channel, EmbedObject message) {
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
     * @param channel The private channel you are sending a message to
     * @param message The message to send
     */
    public void sendMessageToChannel(IPrivateChannel channel, EmbedObject message) {
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

}

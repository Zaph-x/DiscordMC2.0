package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.Map;
import java.util.TreeMap;

public class ChannelManager {

    private static ChannelManager instance;
    private IDiscordClient client = DiscordClientManager.getInstance().getClient();
    private TreeMap<String, Long> channelMap;
    private Main main = Main.getInstance();
    private FileConfiguration config = main.getConfig();

    private ChannelManager() {
    }

    public static ChannelManager getInstance() {
        return instance == null ? instance = new ChannelManager() : instance;
    }

    public Map<String, Long> mapChannels() {
        IGuild guild = client.getGuildByID(config.getLong("discord.guild-id"));
        channelMap.clear();
        for (IChannel channel : guild.getChannels()) {
            channelMap.put(channel.getName(), channel.getLongID());
        }
        return channelMap;
    }

    public void addChannel(ChannelCreateEvent e) {
        IChannel channel = e.getChannel();
        channelMap.put(channel.getName(),channel.getLongID());
    }

    public void removeChannel(ChannelDeleteEvent e) {
        channelMap.remove(e.getChannel().getName());
    }

    public IChannel getChannel(String name) {
        return channelMap.get(name) != null ? client.getChannelByID(channelMap.get(name)) : null;
    }

    public IChannel getChannel(DiscordChannelTypes types) {
        return client.getChannelByID(types.getID());
    }

    public IChannel getChannelForType(DiscordChannelTypes type) {
        return client.getChannelByID(config.getLong(type.getPath()));
    }

    public boolean isChannel(IChannel channel, long ID) {
        return channel.getLongID() == ID;
    }

    public boolean isChannel(IChannel target, IChannel channel) {
        return target == channel;
    }

}

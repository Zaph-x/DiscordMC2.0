package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public enum DiscordChannelTypes {

    /**
     * The log channel
     */
    LOG("discord.log-channel"),
    /**
     * The audit log channel
     */
    AUDIT_LOG("discord.audit-log-channel"),
    /**
     * The announcement channel
     */
    ANNOUNCE("discord.announce-channel"),
    /**
     * The rules channel
     */
    RULES("discord.rules-channel"),
    /**
     * The reports channel
     */
    REPORTS("discord.reports-channel"),
    /**
     * The suggestions channel
     */
    SUGGESTIONS("discord.suggestions-channel");

    /**
     * The path of the channel
     */
    private String path;
    /**
     * The discord client
     */
    private IDiscordClient client = DiscordClientManager.getInstance().getClient();

    /**
     * The constructor of the channel type
     * @param path The path of the channel
     */
    DiscordChannelTypes(String path) {
        this.path = path;
    }

    /**
     * Getter method of the channel
     * @return the channel type it is called on
     */
    public IChannel getChannel() {
        return client.getChannelByID(Dizcord.getInstance().getConfig().getLong(this.path));
    }

    /**
     * Getter message of the channel path
     * @return The channel path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter method of a channel ID
     * @return The id of a channel
     */
    public long getID() {
        return Dizcord.getInstance().getConfig().getLong(this.path);
    }
}

package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public enum DiscordChannelTypes {

    LOG("discord.log-channel"),
    AUDIT_LOG("discord.audit-log-channel"),
    ANNOUNCE("discord.announce-channel"),
    RULES("discord.rules-channel"),
    REPORTS("discord.reports-channel"),
    SUGGESTIONS("discord.suggestions-channel");

    private String path;
    private IDiscordClient client = DiscordClientManager.getInstance().getClient();

    DiscordChannelTypes(String path) {
        this.path = path;
    }

    public IChannel getChannel() {
        return client.getChannelByID(Main.getInstance().getConfig().getLong(this.path));
    }

    public String getPath() {
        return this.path;
    }

    public long getID() {
        return Main.getInstance().getConfig().getLong(this.path);
    }
}

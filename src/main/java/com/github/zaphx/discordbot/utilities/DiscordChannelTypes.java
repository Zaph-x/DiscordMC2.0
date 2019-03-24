package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;

public enum DiscordChannelTypes {

    LOG("discord.log-channel"),
    AUDIT_LOG("discord.audit-log-channel"),
    ANNOUNCE("discord.announce-channel"),
    RULES("discord.rules-channel"),
    REPORTS("discord.reports-channel"),
    SUGGESTIONS("discord.suggestions-channel");

    private String path;
    private DiscordClient client = DiscordClientManager.getInstance().getClient();

    DiscordChannelTypes(String path) {
        this.path = path;
    }

    public TextChannel getChannel() {
        return client.getChannelById(Snowflake.of(Dizcord.getInstance().getConfig().getLong(this.path))).cast(TextChannel.class).block();
    }

    public String getPath() {
        return this.path;
    }

    public Snowflake getId() {
        return Snowflake.of(Dizcord.getInstance().getConfig().getLong(this.path));
    }
}

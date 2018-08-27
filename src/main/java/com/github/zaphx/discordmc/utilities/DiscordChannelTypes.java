package com.github.zaphx.discordmc.utilities;

import com.github.zaphx.discordmc.Main;

public enum DiscordChannelTypes {

    LOG("discord.log-channel"),
    ANNOUNCE("discord.announce-channel"),
    RULES("discord.rules-channel"),
    REPORTS("discord-reports-channel"),
    SUGGESTIONS("discord.suggestions-channel");

    private String path;

    DiscordChannelTypes(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public long getID() {
        return Main.getInstance().getConfig().getLong(this.path);
    }
}

package com.github.zaphx.discordmc.utilities;

public enum DiscordChannelTypes {

    LOG("settings.log-channel"),
    ANNOUNCE("settings.announce-channel"),
    RULES("settings.rules-channel"),
    REPORTS("settings-reports-channel");

    private String path;

    DiscordChannelTypes(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}

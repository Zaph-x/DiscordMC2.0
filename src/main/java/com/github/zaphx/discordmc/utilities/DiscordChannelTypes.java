package com.github.zaphx.discordmc.utilities;

import com.github.zaphx.discordmc.Main;

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

    public long getID() {
        return Main.getInstance().getConfig().getLong(this.path);
    }
}

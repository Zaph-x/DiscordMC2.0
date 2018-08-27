package com.github.zaphx.discordmc.trello;

import com.github.zaphx.discordmc.utilities.DiscordChannelTypes;
import com.github.zaphx.discordmc.utilities.SQLManager;

public class TrelloManager {

    private static TrelloManager instance;
    private SQLManager sql = SQLManager.getInstance();

    private final long REPORTS_CHANNEL = DiscordChannelTypes.REPORTS.getID();

    private TrelloManager() {
    }

    public static TrelloManager getInstance() {
        return instance == null ? new TrelloManager() : instance;
    }



}

package com.github.zaphx.discordbot.trello;

public enum TrelloType {
    /**
     * The suggestion trello type
     */
    SUGGESTION("trello.suggestions"),
    /**
     * The issue report trello type
     */
    ISSUE("trello.issues");

    /**
     * The path to look in
     */
    private String path;

    /**
     * A trello channel type. This is essentially the type of trello event to look for. The path is provided in the constructor
     * @param path
     */
    TrelloType(String path) {
        this.path = path;
    }

    /**
     * Gets the trello board ID
     * @return
     */
    public String getBoardID() {
        return this.path;
    }
}

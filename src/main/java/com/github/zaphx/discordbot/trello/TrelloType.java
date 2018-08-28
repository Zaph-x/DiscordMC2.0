package com.github.zaphx.discordbot.trello;

public enum TrelloType {

    SUGGESTION("trello.suggestions"),
    ISSUE("trello.issues");

    private String path;

    TrelloType(String path) {
        this.path = path;
    }

    public String getBoardID() {
        return this.path;
    }
}

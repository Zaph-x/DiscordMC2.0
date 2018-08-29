package com.github.zaphx.discordbot.discord.commandhandler;

public enum CommandExitCode {

    /**
     * This means the command was successfully executed with correct arguments
     */
    SUCCESS(0),
    /**
     * This is when something goes wrong in every possible way
     */
    ERROR(1),
    /**
     * If the user is missing an argument or something
     */
    INVALID_SYNTAX(2),
    /**
     * This should only be return if the user doesn't have permission
     */
    INSUFFICIENT_PERMISSIONS(3);

    private int code;

    CommandExitCode(int code) {
        this.code = code;
    }

    protected int getCode() {
        return this.code;
    }
}

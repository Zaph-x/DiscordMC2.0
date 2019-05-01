package com.github.zaphx.discordbot.api.commandhandler;

public enum CommandExitCode {

    /**
     * This is returned if the command was successfully executed with the correct arguments
     */
    SUCCESS(0),
    /**
     * This is returned when an exception is caught
     */
    ERROR(1),
    /**
     * If the user is missing an argument or something
     */
    INVALID_SYNTAX(2),
    /**
     * This should only be returned if the user doesn't have permission
     */
    INSUFFICIENT_PERMISSIONS(3),
    /**
     * This is returned if the bot does not have permission to perform an action
     */
    CLIENT_INSUFFICIENT_PERMISSIONS(4),
    /**
     * Returned when there is no command with that name
     */
    NO_SUCH_COMMAND(5)
    ;

    /**
     * The exit code
     */
    private int code;

    /**
     * Constructor of the enum
     * @param code The exit code to exit with
     */
    CommandExitCode(int code) {
        this.code = code;
    }

    /**
     * Getter method for the exit code
     * @return
     */
    protected int getCode() {
        return this.code;
    }
}

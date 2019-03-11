package com.github.zaphx.discordbot.utilities;

public class ArgumentException extends IllegalArgumentException {

    public ArgumentException() {
        super();
    }

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = -3842242432492320L;

}

package com.github.zaphx.discordbot.utilities;

import java.util.regex.Pattern;

public enum RegexPattern {

    /**
     * The time pattern
     */
    TIME("((\\d+[dhm][\\s]?){1,3})"),
    /**
     * The user pattern
     */
    USER("(<@(!)?(\\d+)>)"),
    /**
     * The date pattern
     */
    DATE("([0]?[1-9]|[1|2][0-9]|[3][0|1])[\\s./-]([0]?[1-9]|[1][0-2])[\\s./-]([0-9]{4}|[0-9]{2})(\\s([0-2][0-9])\\s?([0-5][0-9]))?"),
    /**
     * The server advertisement pattern
     */
    SERVER_ADVERTISEMENT("((http(s)?://)?((www.)?discord.gg/|(www.)?discordapp.com/invite))"),
    /**
     * The IP pattern
     */
    IP("(?:[0-9]{1,3}([\\.]|dot|\\(dot\\)|\\(\\))){3}[0-9]{1,3}(:[0-9]{5})?"),
    /**
     * The token pattern
     */
    TOKEN("([a-zA-Z0-9]{24}\\.[a-zA-Z0-9]{6}\\.[a-zA-Z0-9_\\-]{27}|mfa\\.[a-zA-Z0-9_\\-]{84})"),
    /**
     * The emotes pattern
     */
    EMOTES("([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])"),
    /**
     * The role pattern
     */
    ROLE("(<@(&)?(\\d+)>)");

    /**
     * The pattern to get
     */
    private Pattern pattern;

    /**
     * The regex pattern constructor
     * @param pattern The pattern to construct
     */
    RegexPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public static Pattern fromString(String pattern) {
        return Pattern.compile("\\b"+pattern+"\\b");
    }

    /**
     * Getter method of the pattern
     * @return The regex pattern specified
     */
    public Pattern getPattern() {return this.pattern;}

    /**
     * Getter method to get the pattern as a string
     * @return The pattern as a string
     */
    public String toString() {return this.pattern.pattern();}
}

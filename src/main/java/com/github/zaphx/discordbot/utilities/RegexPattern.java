package com.github.zaphx.discordbot.utilities;

import java.util.regex.Pattern;

public enum RegexPattern {

    TIME("((\\d+[dhm][\\s]?){1,3})"),
    USER("(<@(!)?(\\d+)>)"),
    DATE("([0]?[1-9]|[1|2][0-9]|[3][0|1])[\\s./-]([0]?[1-9]|[1][0-2])[\\s./-]([0-9]{4}|[0-9]{2})(\\s([0-2][0-9])\\s?([0-5][0-9]))?"),
    SERVER_ADVERTISEMENT("((http(s)?://)?((www.)?discord.gg/|(www.)?discordapp.com/invite))"),
    IP("(?:[0-9]{1,3}([\\.]|dot|\\(dot\\)|\\(\\))){3}[0-9]{1,3}(:[0-9]{5})?"),
    TOKEN("([a-zA-Z0-9]{24}\\.[a-zA-Z0-9]{6}\\.[a-zA-Z0-9_\\-]{27}|mfa\\.[a-zA-Z0-9_\\-]{84})");

    private Pattern pattern;

    RegexPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public Pattern getPattern() {return this.pattern;}

    public String toString() {return this.pattern.pattern();}
}

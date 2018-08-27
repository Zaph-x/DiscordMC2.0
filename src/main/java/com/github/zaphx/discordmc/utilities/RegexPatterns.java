package com.github.zaphx.discordmc.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatterns {

    public static final Pattern TIME_MATCH = Pattern.compile("((\\d+[dhm][\\s]?){1,3})");
    public static final Pattern USER_MATCH = Pattern.compile("(<@(\\d+)>)");
    public static final Pattern DATE_MATCH = Pattern.compile("([0]?[1-9]|[1|2][0-9]|[3][0|1])[\\s./-]([0]?[1-9]|[1][0-2])[\\s./-]([0-9]{4}|[0-9]{2})(\\s([0-2][0-9])\\s?([0-5][0-9]))?");
    public static final Pattern SERVER_ADVERTISEMENT_MATCH = Pattern.compile("((http(s)?://)?((www.)?discord.gg/|(www.)?discordapp.com/invite))");
    public static final Pattern IP_MATCH = Pattern.compile("(?:[0-9]{1,3}([\\.]|dot|\\(dot\\)|\\(\\))){3}[0-9]{1,3}(:[0-9]{5})?");
    public static final Pattern TOKEN_MATCH = Pattern.compile("([a-zA-Z0-9]{24}\\.[a-zA-Z0-9]{6}\\.[a-zA-Z0-9_\\-]{27}|mfa\\.[a-zA-Z0-9_\\-]{84})");

    public static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    public static boolean lesserThanOrEqualOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        return m.find() && m.group(0).split(" ").length <= length;
    }
}

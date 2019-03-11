package com.github.zaphx.discordbot.utilities;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    public static boolean isMatch(RegexPattern patternToFind, String stringToMatch) {
        return patternToFind.getPattern().matcher(stringToMatch).find();
    }

    public static boolean lesserThanOrEqualOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        return m.find() && m.group(0).split(" ").length <= length;
    }

    public static String stripString(MessageCreateEvent event, Pattern patternToStrip, String stringToStrip) {
        Matcher matcher = patternToStrip.matcher(stringToStrip);
        if (matcher.find()) {
            stringToStrip = stringToStrip.replaceAll(RegexPattern.IP.getPattern().pattern(), "[IP hidden]");
            event.getMember()
                    .map(member -> member.getPrivateChannel()
                            .map(channel -> channel.createMessage("Hidden IP was: " + matcher.group(0)).subscribe()));
        }
        return stringToStrip;
    }
}

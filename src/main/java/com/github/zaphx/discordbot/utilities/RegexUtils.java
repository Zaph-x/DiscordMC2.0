package com.github.zaphx.discordbot.utilities;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.RequestBuffer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    public static boolean lesserThanOrEqualOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        return m.find() && m.group(0).split(" ").length <= length;
    }

    public static String stripString(MessageEvent event, Pattern patternToStrip, String stringToStrip) {
        Matcher matcher = patternToStrip.matcher(stringToStrip);
        if (matcher.find()) {
            stringToStrip = stringToStrip.replaceAll(RegexPattern.IP.getPattern().pattern(), "[IP hidden]");
            RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage("Hidden IP was: " + matcher.group(0)));
        }
        return stringToStrip;
    }
}

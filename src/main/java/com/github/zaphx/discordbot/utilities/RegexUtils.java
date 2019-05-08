package com.github.zaphx.discordbot.utilities;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.RequestBuffer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    /**
     * This method checks if there is a suitable match in the string
     * @param patternToFind The pattern to find
     * @param stringToMatch The string to find the pattern in
     * @return True if the pattern is found
     */
    public static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    /**
     * This method checks if there is a suitable match in the string
     * @param patternToFind The pattern to find
     * @param stringToMatch The string to find the pattern in
     * @return True if the pattern is found
     */
    public static boolean isMatch(RegexPattern patternToFind, String stringToMatch) {
        return patternToFind.getPattern().matcher(stringToMatch).find();
    }

    /**
     * This method will check if a a pattern occours one or more times
     * @param length the lower bound
     * @param patternToFind the pattern to find
     * @param stringToMatch The string to find the pattern in
     * @return true if the pattern occours the amount of times
     */
    public static boolean lesserThanOrEqualOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        return m.find() && m.group(0).split(" ").length <= length;
    }

    /**
     * This method will get the matched word from a regex search
     * @param patternToFind The patther to find
     * @param stringToMatch The string to find the pattern in
     * @return The matched string
     */
    public static String MatchedString(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).group(0);
    }
    /**
     * This method will get the matched word from a regex search
     * @param patternToFind The patther to find
     * @param stringToMatch The string to find the pattern in
     * @return The matched string
     */
    public static String MatchedString(RegexPattern patternToFind, String stringToMatch) {
        return patternToFind.getPattern().matcher(stringToMatch).group(0);
    }

    /**
     * This method will remove a string pattern
     * @param event The event to handle
     * @param patternToStrip The pattern to find
     * @param stringToStrip The string to find the pattern in
     * @return The string with the stripped pattern
     */
    public static String stripString(MessageEvent event, Pattern patternToStrip, String stringToStrip) {
        Matcher matcher = patternToStrip.matcher(stringToStrip);
        if (matcher.find()) {
            stringToStrip = stringToStrip.replaceAll(RegexPattern.IP.getPattern().pattern(), "[IP hidden]");
            RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage("Hidden IP was: " + matcher.group(0)));
        }
        return stringToStrip;
    }
}

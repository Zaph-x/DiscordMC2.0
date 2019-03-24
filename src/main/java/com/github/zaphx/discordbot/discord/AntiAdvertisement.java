package com.github.zaphx.discordbot.discord;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.ChannelManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.utilities.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class AntiAdvertisement {

    private static List<User> allowedUsers = new ArrayList<>();
    private static Dizcord dizcord = Dizcord.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();
    private EmbedManager embedManager = EmbedManager.getInstance();
    private ChannelManager channelManager = ChannelManager.getInstance();

    public AntiAdvertisement() {
    }

    /**
     * Checks and handles an advertisement. This means the bot will delete a message, if it contains either a Discord link or an IP.
     * @param event The event to look in
     */
    public void checkAndHandle(MessageCreateEvent event) {
        Message message = event.getMessage();
        User user = event.getMember().orElseThrow(NullPointerException::new);
        if (matches(message)) {
            if (isAllowed(user)) {
                allowedUsers.remove(user);
                return;
            }
            message.delete().subscribe();
            channelManager.sendMessageToChannel(message.getChannelId(), message.getAuthor().orElseThrow(NullPointerException::new).getMention() + ", :eyes: Advertising isn't cool man...");
            // Send ad log
            messageManager.log(embedManager.reportAdvertisementEmbed(event));
        }
    }

    /**
     * Checks if the message provided matches a ad pattern
     * @param message Event to look in
     * @return True if the message matches, else false
     */
    private boolean matches(Message message) {
        return RegexUtils.isMatch(RegexPattern.SERVER_ADVERTISEMENT.getPattern(), message.getContent().orElse(""))
                || RegexUtils.isMatch(RegexPattern.IP.getPattern(), message.getContent().orElse(""));
    }

    /**
     * Checks if a user is allowed to post an advertisement
     * @param user The user to check
     * @return True if the user is allowed to advertise, else false
     */
    private boolean isAllowed(User user) {
        return allowedUsers.contains(user);
    }

    /**
     * Allows users to advertise.
     * @param event The message to look in
     */
    public static void allow(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (message.getUserMentions().count().block() > 0) {
            message.getUserMentions().subscribe(user -> allowedUsers.add(user));
            dizcord.getServer().getScheduler().runTaskLaterAsynchronously(dizcord, () -> {
                message.getChannel().subscribe(messageChannel -> messageChannel.createMessage(":white_check_mark: The mentioned users have been allowed to post an advertisement."));
                message.getUserMentions().subscribe(user -> allowedUsers.remove(user));
            },30L * 20);
        } else {
            message.getChannel().subscribe(c -> c.createMessage(":x: You must tag at least one user to allow them to post an advertisement."));
        }
    }
}

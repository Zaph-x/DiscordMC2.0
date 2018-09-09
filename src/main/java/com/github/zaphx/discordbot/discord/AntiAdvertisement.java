package com.github.zaphx.discordbot.discord;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.utilities.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public class AntiAdvertisement {

    private static List<IUser> allowedUsers = new ArrayList<>();
    private static Dizcord dizcord = Dizcord.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();
    private EmbedManager embedManager = EmbedManager.getInstance();

    public AntiAdvertisement() {
    }

    public void checkAndHandle(MessageEvent event) {
        IMessage message = event.getMessage();
        IUser user = event.getAuthor();
        if (matches(message)) {
            if (isAllowed(user)) {
                allowedUsers.remove(user);
                return;
            }
            RequestBuffer.request(message::delete);
            message.reply(":eyes: Advertising isn't cool man...");
            // Send ad log
            messageManager.log(embedManager.reportAdvertisementEmbed(event));
        }
    }

    private boolean matches(IMessage message) {
        return RegexUtils.isMatch(RegexPattern.SERVER_ADVERTISEMENT.getPattern(), message.getContent())
                || RegexUtils.isMatch(RegexPattern.IP.getPattern(), message.getContent());
    }

    private boolean isAllowed(IUser user) {
        return allowedUsers.contains(user);
    }

    public static void allow(MessageEvent event) {
        IMessage message = event.getMessage();
        IChannel channel = event.getChannel();
        RequestBuffer.request(message::delete);
        if (message.getMentions().size() > 0) {
            allowedUsers.addAll(message.getMentions());
            dizcord.getServer().getScheduler().runTaskLaterAsynchronously(dizcord, () -> {
                RequestBuffer.request(() -> channel.sendMessage(":white_check_mark: The mentioned users have been allowed to post an advertisement."));
                allowedUsers.removeAll(message.getMentions());
            },30L * 20);
        } else {
            RequestBuffer.request(() -> channel.sendMessage(":x: You must tag at least one user to allow them to post an advertisement."));
        }
    }
}

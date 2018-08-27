package com.github.zaphx.discordmc.discord;

import com.github.zaphx.discordmc.Main;
import com.github.zaphx.discordmc.utilities.DiscordChannelTypes;
import com.github.zaphx.discordmc.utilities.Loggable;
import com.github.zaphx.discordmc.utilities.RegexPatterns;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public class AntiAdvertisement implements Loggable {

    private static List<IUser> allowedUsers = new ArrayList<>();
    private static Main main = Main.getInstance();

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
            sendLogMessage(event);
        }
    }

    private boolean matches(IMessage message) {
        return RegexPatterns.isMatch(RegexPatterns.SERVER_ADVERTISEMENT_MATCH, message.getContent())
                || RegexPatterns.isMatch(RegexPatterns.IP_MATCH, message.getContent());
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
            main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> {
                RequestBuffer.request(() -> channel.sendMessage(":white_check_mark: The mentioned users have been allowed to post an advertisement."));
                allowedUsers.removeAll(message.getMentions());
            },30L * 20);
        } else {
            RequestBuffer.request(() -> channel.sendMessage(":x: You must tag at least one user to allow them to post an advertisement."));
        }
    }

    @Override
    public void sendLogMessage(MessageEvent event) {
        IChannel logChannel = DiscordChannelTypes.LOG.getID() != 0 ? event.getGuild().getChannelByID(DiscordChannelTypes.LOG.getID()) : null;
        if (logChannel == null) {
           RequestBuffer.request(() -> event
                   .getGuild()
                   .getOwner()
                   .getOrCreatePMChannel()
                   .sendMessage(":warning: Please set the log channel. An advertisement from "
                           + event.getAuthor().mention() + " was blocked"));
        } else {
            // TODO Send embed
            // RequestBuffer.request(() -> logChannel.sendMessage(EMBED HERE);
        }
    }


}

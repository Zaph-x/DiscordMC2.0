package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.DateUtils;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

public class Mute implements CommandListener {

    private IRole mute;

    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        RequestBuffer.request(() -> event.getMessage().delete());
        if (!commandHandler.clientHasPermission(event, Permissions.MANAGE_ROLES)) {
            return CommandExitCode.CLIENT_INSUFFICIENT_PERMISSIONS;
        }
        if (event.getMessage().getMentions().size() < 1 || args.size() < 3) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        switch (args.get(1).toLowerCase()) {
            case "voice":
                try {
                    IUser target = event.getMessage().getMentions().get(0);
                    String time = getFinalArg(args, 2);
                    long timestamp = DateUtils.parseDateDiff(time, true);
                    String expiry = DateUtils.formatDateDiff(timestamp);
                    String reason = DateUtils.removeTimePattern(time);

                    mute = clientManager.getClient().getRoleByID(Dizcord.getInstance().getConfig().getLong("discord.voice-mute-role"));

                    target.addRole(mute);
                    sql.executeStatementAndPost("INSERT INTO %smutes (id, muter, expires, type) VALUES ('%s','%s','%s','%s')",
                            Dizcord.getInstance().getConfig().getString("sql.prefix"),
                            target.getStringID(),
                            sender.getStringID(),
                            timestamp,
                            mute.getLongID());
                    messageManager.log(embedManager.logMuteEmbed(reason,expiry,sender, target));
                    channelManager.sendMessageToChannel(target.getOrCreatePMChannel(), embedManager.muteEmbed(reason,expiry,sender));

                    return CommandExitCode.SUCCESS;
                } catch (Exception e) {
                    commandHandler.handleException(e);
                    return CommandExitCode.ERROR;
                }
            case "chat":
                try {
                    IUser target = event.getMessage().getMentions().get(0);
                    String time = getFinalArg(args, 2);
                    long timestamp = DateUtils.parseDateDiff(time, true);
                    String expiry = DateUtils.formatDateDiff(timestamp);
                    String reason = DateUtils.removeTimePattern(time);

                    mute = clientManager.getClient().getRoleByID(Dizcord.getInstance().getConfig().getLong("discord.mute-role"));

                    target.addRole(mute);
                    sql.executeStatementAndPost("INSERT INTO %smutes (id, muter, expires, type) VALUES ('%s','%s','%s','%s')",
                            Dizcord.getInstance().getConfig().getString("sql.prefix"),
                            target.getStringID(),
                            sender.getStringID(),
                            timestamp,
                            mute.getLongID());
                    messageManager.log(embedManager.logMuteEmbed(reason,expiry,sender, target));
                    channelManager.sendMessageToChannel(target.getOrCreatePMChannel(), embedManager.muteEmbed(reason,expiry,sender));
                    return CommandExitCode.SUCCESS;
                } catch (Exception e) {
                    commandHandler.handleException(e);
                    return CommandExitCode.ERROR;
                }
            default:
                return CommandExitCode.INVALID_SYNTAX;
        }
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Allows a staff member to mute a user for a certain amount of time. Please note that the mute expiration is checked every hour.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix+"mute @<user> <time> <reason> - Mutes a user with the provided reason";
    }

    public static String getFinalArg(final List<String> args, final int start) {
        final StringBuilder bldr = new StringBuilder();
        for (int i = start; i < args.size(); i++) {
            if (i != start) {
                bldr.append(" ");
            }
            bldr.append(args.get(i));
        }
        return bldr.toString();
    }


}

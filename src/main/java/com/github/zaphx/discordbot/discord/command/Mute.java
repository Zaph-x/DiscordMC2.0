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

    /**
     * The role to assign
     */
    private IRole mute;

    /**
     * This command will allow a staff member to mute another user. This can be a temporary mute or a permanent mute.
     * @param sender The command sender.
     * @param command The command used.
     * @param args The arguments provided by the sender.
     * @param destination The channel the message should be sent to. By default this is the channel the command was received in.
     * @param event The event provided by discord.
     * @return A {@link CommandExitCode} from the execution result of the command
     */
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

                    return SetMuted(sender, target, timestamp, expiry, reason);
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

                    return SetMuted(sender, target, timestamp, expiry, reason);
                } catch (Exception e) {
                    commandHandler.handleException(e);
                    return CommandExitCode.ERROR;
                }
            default:
                return CommandExitCode.INVALID_SYNTAX;
        }
    }

    /**
     * This method will finalize the mute and log it in the SQL database, the log channel as well as handle a message to the muted
     * @param sender The sender of the command
     * @param target The target of the command
     * @param timestamp The timestamp of the mute
     * @param expiry The expiry date of the mute
     * @param reason The reason for the mute
     * @return
     */
    @NotNull
    private CommandExitCode SetMuted(IUser sender, IUser target, long timestamp, String expiry, String reason) {
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
    }

    /**
     * Gets the command description for the help message
     * @return The command description of what the command does.
     */
    @Override
    public @NotNull String getCommandDescription() {
        return "Allows a staff member to mute a user for a certain amount of time. Please note that the mute expiration is checked every hour.";
    }

    /**
     * Gets the command usage message for the help message
     * @return The command usage message
     */
    @Override
    public @NotNull String getCommandUsage() {
        return prefix+"mute @<user> <time> <reason> - Mutes a user with the provided reason.";
    }

    /**
     * This method will get the final argument from the command arguments
     * @param args The arguments from the command
     * @param start The start index
     * @return A concatenated string of the arguments
     */
    private static String getFinalArg(final List<String> args, final int start) {
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

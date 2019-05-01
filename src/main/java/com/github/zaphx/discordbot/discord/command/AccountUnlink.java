package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class AccountUnlink implements CommandListener {

    /**
     * This command will unlink a minecraft and a discord account. This can only be done if the {@link AccountLink} command has been executed
     * @param sender The command sender.
     * @param command The command used.
     * @param args The arguments provided by the sender.
     * @param destination The channel the message should be sent to. By default this is the channel the command was received in.
     * @param event The event provided by discord.
     * @return A {@link CommandExitCode} from the execution result of the command
     */
    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        if (args.size() == 1 && sender.getPermissionsForGuild(destination.getGuild()).contains(Permissions.ADMINISTRATOR)) {
            if (args.get(0).matches(RegexPattern.USER.toString())) {
                long id;
                try {
                    id = Long.parseLong(args.get(0));
                } catch (NumberFormatException ex) {
                    return CommandExitCode.INVALID_SYNTAX;
                }
                if (sql.isUserLinked(args.get(0))) {
                    sql.executeStatementAndPost("DELETE FROM %slinks WHERE discord = %s", sql.prefix, id);
                    destination.sendMessage(embedManager.userUnlinked());
                    messageManager.log(embedManager.userUnlinked());
                    return CommandExitCode.SUCCESS;
                } else {
                    destination.sendMessage(embedManager.userNotLinked());
                    messageManager.log(embedManager.userUnlinked());
                    return CommandExitCode.SUCCESS;
                }

            } else {
                return CommandExitCode.INVALID_SYNTAX;
            }
        } else if (args.size() == 0) {
            String id = sender.getStringID();
            if (sql.isUserLinked(id)) {
                sql.executeStatementAndPost("DELETE FROM %slinks WHERE discord = %s", sql.prefix, id);
                destination.sendMessage(embedManager.selfUnlinked());
                return CommandExitCode.SUCCESS;
            } else {
                destination.sendMessage(embedManager.selfNotLinked());
                return CommandExitCode.SUCCESS;
            }
        } else {
            return CommandExitCode.INSUFFICIENT_PERMISSIONS;
        }
    }

    /**
     * Gets the command description for the help message
     * @return The command description of what the command does.
     */
    @Override
    public @NotNull String getCommandDescription() {
        return "Unlink two linked accounts. This command requires your account to be linked to a minecraft account.";
    }

    /**
     * Gets the command usage message for the help message
     * @return The command usage message
     */
    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "unlinkaccount\n" + prefix + "unlinkacount @<user>";
    }
}

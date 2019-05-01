package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.discord.AntiAdvertisement;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import com.github.zaphx.discordbot.utilities.RegexUtils;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class AdAllow implements CommandListener {

    /**
     * This command will allow a user to send an advertisement in the form of an IP.
     * @param sender The command sender.
     * @param command The command used.
     * @param args The arguments provided by the sender.
     * @param destination The channel the message should be sent to. By default this is the channel the command was received in.
     * @param event The event provided by discord.
     * @return A {@link CommandExitCode} from the execution result of the command
     */
    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        if (!commandHandler.userHasPermission(event, Permissions.MANAGE_MESSAGES)) {
            return CommandExitCode.INSUFFICIENT_PERMISSIONS;
        }
        if (event.getMessage().getMentions().size() < 1) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        boolean isValid = true;
        for (String s : args) {
            if (RegexUtils.isMatch(RegexPattern.USER.getPattern(), s)) {
                isValid = false;
                break;
            }
        }
        if (!isValid) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        AntiAdvertisement.allow(event);
        return CommandExitCode.SUCCESS;
    }

    /**
     * Gets the command description for the help message
     * @return The command description of what the command does.
     */
    @Override
    public @NotNull String getCommandDescription() {
        return "Allows one or more users to advertise in the global chat. Their right to advertise will be revoked after 30 seconds.";
    }

    /**
     * Gets the command usage message for the help message
     * @return The command usage message
     */
    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "AdAllow @<list of users> - Allows the listed users to advertise.";
    }
}

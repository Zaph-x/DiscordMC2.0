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

    @Override
    public @NotNull String getCommandDescription() {
        return "Allows one or more users to advertise in the global chat. Their right to advertise will be revoked after 30 seconds.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "AdAllow @<list of users> - Allows the listed users to advertise.";
    }
}

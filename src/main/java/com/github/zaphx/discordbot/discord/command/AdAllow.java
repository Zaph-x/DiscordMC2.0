package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.discord.AntiAdvertisement;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import com.github.zaphx.discordbot.utilities.RegexUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AdAllow implements CommandListener {
    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        if (!commandHandler.userHasPermission(event, Permission.MANAGE_MESSAGES)) {
            return CommandExitCode.INSUFFICIENT_PERMISSIONS;
        }
        if (Objects.requireNonNull(event.getMessage().getUserMentions().collectList().block()).size() < 1) {
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

package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class Event implements CommandListener {
    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        String name = "Event";
        IRole role = rolesManager.getRole(name);
        if (args.size() != 1) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        switch (args.get(0)) {
            case "join":
                if (!sender.hasRole(role)) {
                    sender.addRole(role);
                    destination.sendMessage(embedManager.roleJoinEmbed(name));
                } else {
                    destination.sendMessage(embedManager.roleAlreadyActive(name));
                }
                break;
            case "leave":
                if (sender.hasRole(role)) {
                    sender.removeRole(role);
                    destination.sendMessage(embedManager.roleLeaveEmbed(name));
                } else {
                    destination.sendMessage(embedManager.roleNotActive(name));
                }
                break;
            case "on":
                role.changeMentionable(true);
                destination.sendMessage(embedManager.roleMentionableChanged(name, true));
                break;
            case "off":
                role.changeMentionable(false);
                destination.sendMessage(embedManager.roleMentionableChanged(name, false));
                break;
            case "assignall":
                if (sender.getPermissionsForGuild(destination.getGuild()).contains(Permissions.ADMINISTRATOR)) {
                    for (IUser user : destination.getGuild().getUsers()) {
                        user.addRole(role);
                    }
                    destination.sendMessage(embedManager.roleAddedToEveryone(name));
                } else {
                    return CommandExitCode.INSUFFICIENT_PERMISSIONS;
                }
                break;
            default:
                return CommandExitCode.INVALID_SYNTAX;
        }
        return CommandExitCode.SUCCESS;
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Allows a user to join or leave the event ping role";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "events join|leave\n" + prefix + "events assignall\n" + prefix + "events on|off";
    }
}

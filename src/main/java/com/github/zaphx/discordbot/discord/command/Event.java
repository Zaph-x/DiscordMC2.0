package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Event implements CommandListener {
    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        String name = "Event";
        Role role;
        role = clientManager.getClient().getRoleById(clientManager.GUILD_SNOWFLAKE,rolesManager.getRole(name)).block();
        if (args.size() != 1) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        Member member = sender.asMember(clientManager.GUILD_SNOWFLAKE).block();
        if (role != null) {
            if (member == null) {
                destination.createMessage(":x: Could not resolve member").subscribe();
                return CommandExitCode.ERROR;
            }
            switch (args.get(0)) {
                case "join":
                    if (!member.getRoles().hasElement(role).block()) {
                        member.addRole(role.getId()).subscribe();
                        destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.roleJoinEmbed(name))).subscribe();
                    } else {
                        destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.roleAlreadyActive(name))).subscribe();
                    }
                    break;
                case "leave":
                    if (member.getRoles().hasElement(role).block()) {
                        member.removeRole(role.getId()).subscribe();
                        destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.roleLeaveEmbed(name))).subscribe();
                    } else {
                        destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.roleNotActive(name))).subscribe();
                    }
                    break;
                case "on":
                    role.edit(spec -> spec.setMentionable(true)).subscribe();
                    destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.roleMentionableChanged(name, true))).subscribe();
                    break;
                case "off":
                    role.edit(spec -> spec.setMentionable(false)).subscribe();
                    destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.roleMentionableChanged(name, false))).subscribe();
                    break;
                case "assignall":
                    if (Objects.requireNonNull(member.getBasePermissions().block()).contains(Permission.ADMINISTRATOR)) {
                        for (Member user : Objects.requireNonNull(Objects.requireNonNull(event.getGuild().block()).getMembers().collectList().block())) {
                            user.addRole(role.getId()).subscribe();
                        }
                        destination.createMessage(m -> m.setEmbed(embedManager.roleAddedToEveryone(name)));
                    } else {
                        return CommandExitCode.INSUFFICIENT_PERMISSIONS;
                    }
                    break;
                default:
                    return CommandExitCode.INVALID_SYNTAX;
            }
            return CommandExitCode.SUCCESS;
        }else {
            destination.createMessage(":x: Event role could not be found.").subscribe();
            return CommandExitCode.ERROR;
        }
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

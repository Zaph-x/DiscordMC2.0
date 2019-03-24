package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AccountUnlink implements CommandListener {

    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        if (args.size() == 1 && sender.asMember(event.getGuildId().orElseThrow(NullPointerException::new)).blockOptional().orElseThrow(NullPointerException::new)
                .getBasePermissions().blockOptional().orElseThrow(NullPointerException::new).contains(Permission.ADMINISTRATOR)) {
            if (args.get(0).matches(RegexPattern.USER.toString())) {
                long id;
                try {
                    id = Long.parseLong(args.get(0));
                } catch (NumberFormatException ex) {
                    return CommandExitCode.INVALID_SYNTAX;
                }
                if (sql.isUserLinked(args.get(0))) {
                    sql.executeStatementAndPost("DELETE FROM %slinks WHERE discord = %s", sql.prefix, id);
                    destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.userUnlinked())).subscribe();
                    messageManager.log(embedManager.userUnlinked());
                    return CommandExitCode.SUCCESS;
                } else {
                    destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.userNotLinked())).subscribe();
                    messageManager.log(embedManager.userUnlinked());
                    return CommandExitCode.SUCCESS;
                }

            } else {
                return CommandExitCode.INVALID_SYNTAX;
            }
        } else if (args.size() == 0) {
            String id = sender.getId().asString();
            if (sql.isUserLinked(id)) {
                sql.executeStatementAndPost("DELETE FROM %slinks WHERE discord = %s", sql.prefix, id);
                destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.selfUnlinked())).subscribe();
                return CommandExitCode.SUCCESS;
            } else {
                destination.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(embedManager.selfNotLinked())).subscribe();
                return CommandExitCode.SUCCESS;
            }
        } else {
            return CommandExitCode.INSUFFICIENT_PERMISSIONS;
        }
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Unlink two linked accounts. This command requires your account to be linked to a minecraft account.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "unlinkaccount\n" + prefix + "unlinkacount @<user>";
    }
}

package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import com.github.zaphx.discordbot.utilities.RegexUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Warn implements CommandListener {

    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        event.getMessage().delete().subscribe();
        if (commandHandler.userHasPermission(event, Permission.MANAGE_MESSAGES)) {
            String reason;
            User warned = event.getMessage().getUserMentions().toStream().findFirst().get();
            StringBuilder builder = new StringBuilder();
            if (event.getMessage().getUserMentions().toStream().count() < 1) {
                return CommandExitCode.INVALID_SYNTAX;
            }
            if (!RegexUtils.isMatch(RegexPattern.USER, args.get(0))) {
                return CommandExitCode.INVALID_SYNTAX;
            }
            if (args.size() > 1) {
                for (int i = 1; i < args.size(); i++) builder.append(args.get(i)).append(" ");
                reason = builder.toString().trim();
            } else if (args.size() == 1) {
                reason = "No reason provided.";
            } else {
                return CommandExitCode.INVALID_SYNTAX;
            }
            String sqlprefix = Dizcord.getInstance().getConfig().getString("sql.prefix");
            sql.executeStatementAndPost("INSERT INTO %s" + "%s (id, reason, warnee) VALUES ('%s','%s','%s')", sqlprefix, "warnings", warned.getId().asString(), reason, warned.getId().asString());
            messageManager.log(embedManager.warningToChannel(warned, sender, reason, event.getGuild().block(),destination));
            channelManager.sendMessageToChannel(warned.getPrivateChannel().block(), embedManager.warningToUser(warned, sender, reason, event.getGuild()));
            return CommandExitCode.SUCCESS;
        }
        return CommandExitCode.INSUFFICIENT_PERMISSIONS;
    }


    @Override
    public @NotNull String getCommandDescription() {
        return "This command allows a staff member to warn a user for a given reason. The warning will be logged for staff to look at later.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "warn @<user> <reason> - Warns a user.";
    }
}

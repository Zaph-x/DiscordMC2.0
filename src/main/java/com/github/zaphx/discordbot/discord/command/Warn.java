package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import com.github.zaphx.discordbot.utilities.RegexUtils;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

public class Warn implements CommandListener {

    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        RequestBuffer.request(() -> event.getMessage().delete());
        if (commandHandler.userHasPermission(event, Permissions.MANAGE_MESSAGES)) {
            String reason;
            IUser warned = event.getMessage().getMentions().get(0);
            StringBuilder builder = new StringBuilder();
            if (event.getMessage().getMentions().size() < 1) {
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
            sql.executeStatementAndPost("INSERT INTO %s" + "%s (id, reason, warnee) VALUES ('%s','%s','%s')", sqlprefix, "warnings", warned.getStringID(), reason, warned.getStringID());
            messageManager.log(embedManager.warningToChannel(warned, sender, reason, event.getGuild()));
            channelManager.sendMessageToChannel(warned.getOrCreatePMChannel(), embedManager.warningToUser(warned, sender, reason, event.getGuild()));
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
        return prefix + "warn @<user> <reason> - Warns a user";
    }
}

package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.DateUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Mute implements CommandListener {

    private Role mute;

    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        event.getMessage().delete().subscribe();
        if (!commandHandler.clientHasPermission(event, Permission.MANAGE_ROLES)) {
            return CommandExitCode.CLIENT_INSUFFICIENT_PERMISSIONS;
        }
        if (event.getMessage().getUserMentions().toStream().count() < 1 || args.size() < 3) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        switch (args.get(1).toLowerCase()) {
            case "voice":
                try {
                    String time = getFinalArg(args, 2);
                    long timestamp = DateUtils.parseDateDiff(time, true);
                    String expiry = DateUtils.formatDateDiff(timestamp);
                    String reason = DateUtils.removeTimePattern(time);

                    clientManager.getClient().getRoleById(clientManager.GUILD_SNOWFLAKE,Snowflake.of(Dizcord.getInstance().getConfig().getLong("discord.voice-mute-role"))).subscribe(r -> mute = r);

                    event.getMessage().getUserMentions().subscribe(u -> handleMute(sender, u, timestamp, expiry, reason)); //.getMember().getMentions().get(0);
                    return CommandExitCode.SUCCESS;
                } catch (Exception e) {
                    commandHandler.handleException(e);
                    return CommandExitCode.ERROR;
                }
            case "chat":
                try {
                    String time = getFinalArg(args, 2);
                    long timestamp = DateUtils.parseDateDiff(time, true);
                    String expiry = DateUtils.formatDateDiff(timestamp);
                    String reason = DateUtils.removeTimePattern(time);

                    clientManager.getClient().getRoleById(clientManager.GUILD_SNOWFLAKE,Snowflake.of(Dizcord.getInstance().getConfig().getLong("discord.mute-role"))).subscribe(r -> mute = r);

                    event.getMessage().getUserMentions().subscribe(u -> handleMute(sender, u, timestamp, expiry, reason));
                    return CommandExitCode.SUCCESS;
                } catch (Exception e) {
                    commandHandler.handleException(e);
                    return CommandExitCode.ERROR;
                }
            default:
                return CommandExitCode.INVALID_SYNTAX;
        }
    }

    @NotNull
    private void handleMute(User sender, User target, long timestamp, String expiry, String reason) {
        target.asMember(clientManager.GUILD_SNOWFLAKE).subscribe(member -> member.addRole(mute.getId()));
        sql.executeStatementAndPost("INSERT INTO %smutes (id, muter, expires, type) VALUES ('%s','%s','%s','%s')",
                Dizcord.getInstance().getConfig().getString("sql.prefix"),
                target.getId().asString(),
                sender.getId().asString(),
                timestamp,
                mute.getId().asString());
        messageManager.log(embedManager.logMuteEmbed(reason,expiry,sender, target));
        channelManager.sendMessageToChannel(target.getPrivateChannel().block(), embedManager.muteEmbed(reason, expiry, sender));
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Allows a staff member to mute a user for a certain amount of time. Please note that the mute expiration is checked every hour.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix+"mute @<user> <time> <reason> - Mutes a user with the provided reason.";
    }

    public static String getFinalArg(final List<String> args, final int start) {
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

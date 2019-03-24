package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapMessages implements CommandListener {

    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        double time = System.currentTimeMillis();
        if (!commandHandler.userHasPermission(event, Permission.ADMINISTRATOR)) {
            return CommandExitCode.CLIENT_INSUFFICIENT_PERMISSIONS;
        }
        if (args.size() > 0) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        for (MessageChannel channel : event.getGuild().map(guild -> guild.getChannels().filter(c -> c instanceof TextChannel).cast(MessageChannel.class)).block().toIterable()) {
            for (Message message : channel.getMessagesBefore(event.getMessage().getId()).take(200).toIterable()) {
                sql.executeStatementAndPost("INSERT INTO " + sql.prefix + "messages (id, content, author, author_name, channel) VALUES ('%s','%s','%s','%s','%s') \nON DUPLICATE KEY UPDATE content = " +
                                "'%s'",
                        message.getId().asString(),
                        message.getContent().orElse("").replaceAll("'","¼"),
                        message.getAuthor().get().getId().asString(),
                        message.getAuthor().get().getUsername(),
                        message.getChannel().block().getId().asString(),
                        message.getContent().orElse("").replaceAll("'","¼"));
            }
        }
        double elapsed = System.currentTimeMillis() / time;
        destination.createMessage("Mapped all channels successfully. Time elapsed: " + elapsed + " seconds.").subscribe();
        return CommandExitCode.SUCCESS;
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "This command maps the last 200 messages from each channel, to log them when they are deleted.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "mapmessages - Maps messages.";
    }
}

package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class MapMessages implements CommandListener {
    /**
     * This command will map all messages and store them in an SQL database
     * @param sender The command sender.
     * @param command The command used.
     * @param args The arguments provided by the sender.
     * @param destination The channel the message should be sent to. By default this is the channel the command was received in.
     * @param event The event provided by discord.
     * @return A {@link CommandExitCode} from the execution result of the command
     */
    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        double time = System.currentTimeMillis();
        if (!commandHandler.userHasPermission(event, Permissions.ADMINISTRATOR)) {
            return CommandExitCode.CLIENT_INSUFFICIENT_PERMISSIONS;
        }
        if (args.size() > 0) {
            return CommandExitCode.INVALID_SYNTAX;
        }
        for (IChannel channel : event.getGuild().getChannels()) {
            for (IMessage message : channel.getMessageHistory(200)) {
                sql.executeStatementAndPost("INSERT INTO " + sql.prefix + "messages (id, content, author, author_name, channel) VALUES ('%s','%s','%s','%s','%s') \nON DUPLICATE KEY UPDATE content = " +
                                "'%s'",
                        message.getStringID(),
                        message.getContent().replaceAll("'", "¼"),
                        message.getAuthor().getStringID(),
                        message.getAuthor().getName(),
                        message.getChannel().getStringID(),
                        message.getContent().replaceAll("'", "¼"));
            }
        }
        double elapsed = System.currentTimeMillis() / time;
        destination.sendMessage("Mapped all channels successfully. Time elapsed: " + elapsed + " seconds.");
        return CommandExitCode.SUCCESS;
    }

    /**
     * Gets the command description for the help message
     * @return The command description of what the command does.
     */
    @Override
    public @NotNull String getCommandDescription() {
        return "This command maps the last 200 messages from each channel, to log them when they are deleted.";
    }

    /**
     * Gets the command usage message for the help message
     * @return The command usage message
     */
    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "mapmessages - Maps messages.";
    }
}

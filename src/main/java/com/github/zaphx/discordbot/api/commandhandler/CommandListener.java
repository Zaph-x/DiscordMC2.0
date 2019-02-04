package com.github.zaphx.discordbot.api.commandhandler;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.*;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

/**
 * This is the command listener interface that makes a class an executable command.
 */
public interface CommandListener {

    DiscordClientManager clientManager = DiscordClientManager.getInstance();
    CommandHandler commandHandler = CommandHandler.getInstance();
    EmbedManager embedManager = EmbedManager.getInstance();
    MessageManager messageManager = MessageManager.getInstance();
    ChannelManager channelManager = ChannelManager.getInstance();
    RolesManager rolesManager = RolesManager.getInstance();
    SQLManager sql = SQLManager.getInstance();

    String prefix = Dizcord.getInstance().getConfig().getString("discord.command-prefix");
    /**
     * The method used whenever a command is executed. This will automatically be called when a command is executed.
     * It will return a {@link CommandExitCode} based on how the command was exited.
     * @param sender The command sender.
     * @param command The command used.
     * @param args The arguments provided by the sender.
     * @param destination The channel the message should be sent to. By default this is the channel the command was received in.
     * @param event The event provided by discord.
     * @return This method will return a {@link CommandExitCode} showing how the command was exited.
     */
    CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event);

    /**
     * Used to generate the help message, when the help command is run on the command.
     * @return Returns the help message of the provided command.
     */
    @NotNull
    String getCommandDescription();
    /**
     * Used to generate the usage message in the help embed, when the help command is run on the command.
     * @return Returns the usage message of the provided command.
     */
    @NotNull
    String getCommandUsage();
}

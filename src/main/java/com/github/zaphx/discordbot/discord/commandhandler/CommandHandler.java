package com.github.zaphx.discordbot.discord.commandhandler;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.managers.ChannelManager;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang.exception.ExceptionUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * This class is the command handler for the bot. This is where all commands are handled when received.
 */
public class CommandHandler {

    /**
     * The instance of the channel manager, used to send messages to specific channels.
     */
    private ChannelManager channelManager = ChannelManager.getInstance();
    /**
     * The instance of the embed manager
     */
    private EmbedManager embedManager = EmbedManager.getInstance();
    /**
     * The instance of the command handler.
     */
    private static CommandHandler instance;
    /**
     * The command prefix.
     */
    private String commandPrefix = Main.getInstance().getConfig().getString("discord.command-prefix");
    /**
     * The map where all commands are registered to.
     */
    private static TMap<String, Object> commandMap = new THashMap<>();
    private CommandHandler() {
    }

    /**
     * The method for getting the command handler instance.
     *
     * @return The instance of the command handler.
     */
    public static CommandHandler getInstance() {
        return instance == null ? instance = new CommandHandler() : instance;
    }

    /**
     * Checks a message to see if it is a valid command.
     *
     * @param event The event to check in.
     */
    public void checkForCommand(MessageReceivedEvent event) {
        IUser sender = event.getAuthor();
        IChannel channel = event.getChannel();
        String s = event.getMessage().getContent();
        String[] fullCommand = s.toLowerCase().split(" ");
        if (s.startsWith(commandPrefix.toLowerCase())) {
            if (fullCommand.length == 0) return;
            String command = fullCommand[0].substring(commandPrefix.length());
            List<String> args = new ArrayList<>(Arrays.asList(fullCommand));
            args.remove(0);
            if (s.length() == commandPrefix.length()) return;
            if (commandMap.get(command) != null) {
                CommandExitCode exitCode = ((CommandListener) commandMap.get(command)).onCommand(sender, command, args, channel, event);
                switch (exitCode) {
                    case ERROR:
                        channelManager.sendMessageToChannel(event,embedManager.exception());
                        // Should tell the user that the command encountered an exception
                        break;
                    case INVALID_SYNTAX:
                        channelManager.sendMessageToChannel(event, embedManager.invalidSyntaxEmbed(command));
                        // Should tell the user that the arguments were incorrect
                        break;
                    case INSUFFICIENT_PERMISSIONS:
                        // Should tell the user they don't have the right permissions to perform that command
                        channelManager.sendMessageToChannel(event, embedManager.insufficientPermissions());
                        break;
                    case CLIENT_INSUFFICIENT_PERMISSIONS:
                        channelManager.sendMessageToChannel(event, embedManager.insufficientPermissions());
                        // Should tell the user the client doesn't have the permissions to perform that action
                        break;
                    case NO_SUCH_COMMAND:
                        channelManager.sendMessageToChannel(event, embedManager.invalidCommandEmbed());
                        break;
                    case SUCCESS:
                        // Nothing
                        break;
                    default:
                        // This will never happen
                }
            } else {
                channelManager.sendMessageToChannel(event, embedManager.invalidCommandEmbed());
            }
        }
    }

    /**
     * The method used to register commands in the command handler. These commands are registered to the command map.
     *
     * @param commandName The name of the command being registered.
     * @param command     The command class.
     */
    public void registerCommand(String commandName, Object command) {
        if (command instanceof CommandListener) {
            Main.getInstance().getLogger().log(Level.INFO, "Registering " + commandName + " as a command!");
            commandMap.put(commandName.toLowerCase(), command);
        } else throw new IllegalStateException("Object " + command + " is not an instance of CommandListener");
    }

    /**
     * Gets the command list for the bot
     * @return The command list
     */
    public TMap<String, Object> getCommandMap() {
        return commandMap;
    }

    /**
     * Checks if the user has permission to perform certain actions
     * @param event The event to listen to
     * @param permission The permission to check for
     * @return True if the user has permission
     */
    public boolean userHasPermission(MessageEvent event, Permissions permission) {
        return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(permission);
    }

    /**
     * Checks if the bot has permission to perform a certain action
     * @param event The event to listen to
     * @param permission The permission to check for
     * @return True if the client has permission
     */
    public boolean clientHasPermission(MessageEvent event, Permissions permission) {
        return event.getClient().getOurUser().getPermissionsForGuild(event.getGuild()).contains(permission);
    }

    /**
     * Will send any error to the owner of the bot. This should generally be placed within a catch clause
     * @param e The exception thrown.
     */
    public void handleException(Exception e) {
        DiscordClientManager
                .getInstance()
                .getClient()
                .getApplicationOwner()
                .getOrCreatePMChannel()
                .sendMessage(embedManager.exceptionToOwner(ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e)));
    }
}

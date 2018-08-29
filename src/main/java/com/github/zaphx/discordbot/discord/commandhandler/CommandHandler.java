package com.github.zaphx.discordbot.discord.commandhandler;

import com.github.zaphx.discordbot.Main;
import com.sun.corba.se.impl.io.TypeMismatchException;
import gnu.trove.map.hash.THashMap;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    private static CommandHandler instance;
    private String commandprefix = Main.getInstance().getConfig().getString("discord.command-prefix");
    private Map<String, Object> commandMap = new THashMap<>();

    private CommandHandler() {
    }

    public static CommandHandler getInstance() {
        return instance == null ? instance = new CommandHandler() : instance;
    }

    public void checkForCommand(MessageReceivedEvent event) {
        IUser sender = event.getAuthor();
        IChannel channel = event.getChannel();
        String s = event.getMessage().getContent();
        String[] fullCommand = s.toLowerCase().split(" ");
        if (s.startsWith(commandprefix.toLowerCase())) {
            if (fullCommand.length == 0) return;
            String command = fullCommand[0].substring(commandprefix.length());
            List<String> args = new ArrayList<>(Arrays.asList(fullCommand));
            args.remove(0);
            if (s.length() == commandprefix.length()) return;
            System.out.println("Succeed");
            if (commandMap.get(command) != null) {
                CommandExitCode exitCode = ((CommandListener) commandMap.get(command)).onCommand(sender, command, args, channel);
                if (exitCode.equals(CommandExitCode.ERROR)) {
                    // Should not happen, but it means command not found
                    channel.sendMessage(s);
                } else if (exitCode.equals(CommandExitCode.INVALID_SYNTAX)) {
                    // Should only happen if the user doesn't provide enough arguments or invalid arguments
                    // channel.sendMessage(HELP EMBED)
                } else {
                    // Exit the loop
                }
            }
        }


    }

    public void registerCommand(String commandName, Object command) {
        if (command instanceof CommandListener) {
            System.out.println("Registering " + commandName + " as a command!");
            commandMap.put(commandName.toLowerCase(), command);
        } else throw new TypeMismatchException("Object " + command + " is not an instance of CommandListener");
    }
}

package com.github.zaphx.discordbot.minecraft.commands;

import com.github.zaphx.discordbot.managers.ChannelManager;
import discord4j.core.object.entity.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import static org.bukkit.ChatColor.*;


public class ToDiscord implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TextChannel channel;
        StringBuilder sb;
        ChannelManager channelManager = ChannelManager.getInstance();

        if (!sender.hasPermission("dizcord.admin")) {
            sender.sendMessage(DARK_RED +"You do not have permission to perform this command.");
            return true;
        } else {
            if (args.length < 2) {
                sender.sendMessage(RED +"You did not provide enough arguments to perform the command.\n" +
                        "/todiscord <channel> <message>");
                return true;
            } else if (String.join(" ", args).length() > 2000) {
                sender.sendMessage(RED +"You exceeded the max amount of characters in the message. The limit is 2000 characters.");
                return true;
            } else {
                sb = new StringBuilder();
                channel = channelManager.getChannel(args[0]);


                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                if (channel == null) {
                    sender.sendMessage(RED +"The channel you specified does not exist.");
                    return true;
                } else {
                    channel.createMessage(sb.toString()).subscribe();
                    sender.sendMessage(GREEN + "Message sent");
                    return true;
                }
            }
        }
    }
}

package com.github.zaphx.discordbot.minecraft.commands;

import com.github.zaphx.discordbot.managers.ChannelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;
import static org.bukkit.ChatColor.*;


public class ToDiscord implements CommandExecutor {

    /**
     * This command will send a message to a discord channel
     * @param sender The sender of the command
     * @param command The command being sent
     * @param label The command label
     * @param args The arguments passed to the command
     * @return True if command succeeds, else false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        IChannel channel;
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
                    RequestBuffer.request(() -> channel.sendMessage(sb.toString()));
                    sender.sendMessage(GREEN + "Message sent");
                    return true;
                }
            }
        }
    }
}

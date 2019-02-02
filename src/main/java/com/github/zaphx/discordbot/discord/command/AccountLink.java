package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.managers.InternalsManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import org.bukkit.entity.Player;
import static org.bukkit.ChatColor.*;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

public class AccountLink implements CommandListener {

    private String username;
    private SQLManager sql = SQLManager.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();

    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {

        username = args.get(0);
        int hash = Math.abs(username.hashCode() + sender.getName().hashCode());

        if (args.size() != 1) return CommandExitCode.INVALID_SYNTAX;
        if (Dizcord.getInstance().getServer().getPlayer(username) == null) {
            Player user = Dizcord.getInstance().getServer().getPlayer(username);
            messageManager.hashes.put(hash, sender);
            user.sendMessage(GREEN + "Someone is trying to link their Discord account to this account!\nIf this is you, type the following command: " + YELLOW + "/dizcord link " + hash);
        } else {
            destination.sendMessage("We're sorry, but that player is not online right now. Make sure you spelled the name correctly.");
        }

        return CommandExitCode.SUCCESS;
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Allows a user to link their Discord and Minecraft account. This will also sync roles across Discord and Minecraft\n*You can only link one account!*";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return "ob!linkaccounts <in-game name>";
    }
}

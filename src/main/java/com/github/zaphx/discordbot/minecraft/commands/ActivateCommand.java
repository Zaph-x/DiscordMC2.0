package com.github.zaphx.discordbot.minecraft.commands;

import com.github.zaphx.discordbot.managers.DiscordClientManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ActivateCommand implements CommandExecutor {

    DiscordClientManager clientManager = DiscordClientManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("Invalid command");
            return true;
        }
        if (!sender.hasPermission("discordmc.admin")) {
            sender.sendMessage("Invalid command");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "login":
                if (!clientManager.getClient().isLoggedIn()) {
                    clientManager.login(clientManager.getClient());
                    sender.sendMessage("§aYou logged the bot in!");
                    Bukkit.getLogger().info(sender.getName() + " tried to log the bot in");
                } else {
                    sender.sendMessage("§aBot already logged in");
                }
                break;
            case "logout":
                if (clientManager.getClient().isLoggedIn()) {
                    clientManager.logout(clientManager.getClient());
                    sender.sendMessage("§aYou logged the bot out!");
                    Bukkit.getLogger().info(sender.getName() + " tried to log the bot out");
                } else {
                    sender.sendMessage("§aBot already logged out");
                }
                break;
            default:
                sender.sendMessage("Invalid command");
        }
        return true;
    }
}

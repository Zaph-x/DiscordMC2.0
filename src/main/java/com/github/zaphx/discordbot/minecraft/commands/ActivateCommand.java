package com.github.zaphx.discordbot.minecraft.commands;

import com.github.zaphx.discordbot.managers.DiscordClientManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import static org.bukkit.ChatColor.*;


public class ActivateCommand implements CommandExecutor {

    DiscordClientManager clientManager = DiscordClientManager.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("link") && args.length == 2) {
            int hash;
            try {
                hash = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(RED + "You did not provide a valid hash.");
                return true;
            }
            // Pull hash from TreeMap and link accounts. Then add the link to the database.

        } else if (sender.hasPermission("dizcord.admin")) {
            if (args.length > 1) {
                sender.sendMessage("Invalid command");
                return true;
            } else if (args.length == 0) {
                sender.sendMessage("Too few arguments.");
                return true;
            }

        } else {
            sender.sendMessage("Insufficient permissions.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "login":
                if (!clientManager.getClient().isLoggedIn()) {
                    clientManager.login(clientManager.getClient());
                    sender.sendMessage(GREEN + "You logged the bot in!");
                    Bukkit.getLogger().info(sender.getName() + " tried to log the bot in");
                } else {
                    sender.sendMessage(GREEN + "Bot already logged in");
                }
                break;
            case "logout":
                if (clientManager.getClient().isLoggedIn()) {
                    clientManager.logout(clientManager.getClient());
                    sender.sendMessage(GREEN + "You logged the bot out!");
                    Bukkit.getLogger().info(sender.getName() + " tried to log the bot out");
                } else {
                    sender.sendMessage(GREEN + "Bot already logged out");
                }
                break;
            default:
                sender.sendMessage("Invalid command");
        }
        return true;
    }
}

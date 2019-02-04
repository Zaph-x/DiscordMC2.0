package com.github.zaphx.discordbot.minecraft.commands;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.managers.RolesManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;


public class MainCommand implements CommandExecutor {

    MessageManager messageManager = MessageManager.getInstance();
    DiscordClientManager clientManager = DiscordClientManager.getInstance();
    RolesManager rolesManager = RolesManager.getInstance();
    SQLManager sql = SQLManager.getInstance();
    FileConfiguration config = Dizcord.getInstance().getConfig();

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
            if (!(sender instanceof Player)) {
                sender.sendMessage(RED + "Only players can perform this command!");
                return true;
            } else if (messageManager.hashes.get(hash).getUniqueId() == ((Player) sender).getUniqueId()) {
                Player sender_p = (Player) sender;
                if (!sql.isUserLinked(
                        messageManager.discord.get(hash).getStringID(),
                        sender_p.getUniqueId())) {
                    sql.executeStatementAndPost("INSERT INTO %slinks (id, hash, discord) values ('%s','%s','%s')",
                            sql.prefix, sender_p.getUniqueId().toString(), hash, messageManager.discord.get(hash).getStringID());
                    sender.sendMessage("Your account was linked!");
                    if (sender.hasPermission("dizcord.donator")) messageManager.discord.get(hash).addRole(rolesManager.getRole("Donator"));
                    if (sender.hasPermission("group.builder")) messageManager.discord.get(hash).addRole(rolesManager.getRole("Builder"));
                    if (sender.hasPermission("group.redstone")) messageManager.discord.get(hash).addRole(rolesManager.getRole("Redstone"));
                    if (sender.hasPermission("group.advbuilder")) messageManager.discord.get(hash).addRole(rolesManager.getRole("Advanced Builder"));
                    return true;
                } else {
                    sender.sendMessage(RED + "Your account is already linked!");
                    return true;
                }
            } else {
                sender.sendMessage(RED + "That link does not belong to you!");
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

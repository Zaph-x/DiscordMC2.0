package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AccountLink implements CommandListener {

    private String username;

    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {

        if (args.size() != 1) return CommandExitCode.INVALID_SYNTAX;
        username = args.get(0);
        Player player = Dizcord.getInstance().getServer().getPlayer(username);
        if (player != null) {
            int hash = Math.abs(player.getUniqueId().toString().hashCode() << sender.getId().asString().hashCode());
            if (sql.isUserLinked(sender.getId().asString(), player.getUniqueId())) {
                destination.createMessage(m -> m.setEmbed(embedManager.userAlreadyLinked())).subscribe();
                return CommandExitCode.SUCCESS;
            }
            messageManager.hashes.put(hash, player);
            messageManager.discord.put(hash, sender);
            TextComponent message = new TextComponent(YELLOW + "/dizcord link " + hash);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "/dizcord link " + hash));
            player.sendMessage(GREEN + "Someone is trying to link their Discord account to this account!\nIf this is you, type the following command: ");
            player.spigot().sendMessage(new ComponentBuilder(YELLOW + "/dizcord link " + hash).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dizcord link " + hash)).create());
            messageManager.log(embedManager.userLinked(sender, player.getName()));
        } else {
            destination.createMessage(m->m.setEmbed(embedManager.noPlayerEmbed(username))).subscribe();
        }

        return CommandExitCode.SUCCESS;
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Allows a user to link their Discord and Minecraft account. This will also sync roles across Discord and Minecraft\n*You can only link one account!*";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "!linkaccounts <in-game name>";
    }
}

package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import gnu.trove.map.TMap;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class Help implements CommandListener {

    private CommandHandler commandHandler = CommandHandler.getInstance();

    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {
        event.getMessage().delete().subscribe();
        TMap<String, Object> commands = commandHandler.getCommandMap();

        if (args.size() == 0) {

            sender.getPrivateChannel().subscribe(channel -> channel.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(spec -> {
                spec.setTitle("Full help list").setTimestamp(Instant.now()).setColor(new Color(133, 150, 211));
                for (TMap.Entry entry : commands.entrySet()) {
                    spec.addField(entry.getKey().toString(), ((CommandListener) entry.getValue()).getCommandDescription()
                            + "\n" + ((CommandListener) entry.getValue()).getCommandUsage(), false);
                }
            })));
            return CommandExitCode.SUCCESS;
        } else if (args.size() == 1){
            boolean isCommand = commands.get(args.get(0)) != null;
            if (isCommand) {

                sender.getPrivateChannel().subscribe(channel -> channel.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(spec -> {
                    spec.setTimestamp(Instant.now()).setColor(new Color(133, 150, 211))
                            .setTitle("Command help for "+ args.get(0))
                            .setDescription(((CommandListener) commands.get(args.get(0))).getCommandDescription())
                            .addField("Usage", ((CommandListener) commands.get(args.get(0))).getCommandUsage(), false);
                })));
                return CommandExitCode.SUCCESS;
            } else {
                return CommandExitCode.NO_SUCH_COMMAND;
            }
        } else {
            return CommandExitCode.INVALID_SYNTAX;
        }
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "The base help command. This command will list all other commands in a DM.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "help - Provides full help list.\n" + prefix + "help <command> - Provides help message for a command.";
    }
}

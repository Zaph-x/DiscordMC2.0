package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandHandler;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import gnu.trove.map.TMap;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.time.Instant;
import java.util.List;

public class Help implements CommandListener {

    private CommandHandler commandHandler = CommandHandler.getInstance();

    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {
        RequestBuffer.request(() -> event.getMessage().delete());
        TMap<String, Object> commands = commandHandler.getCommandMap();
        EmbedBuilder embedBuilder = new EmbedBuilder().withTimestamp(Instant.now()).withColor(133, 150, 211);
        if (args.size() == 0) {
            embedBuilder.withTitle("Full help list");
            for (TMap.Entry entry : commands.entrySet()) {
                embedBuilder.appendField(entry.getKey().toString(), ((CommandListener) entry.getValue()).getCommandDescription()
                        + "\n" + ((CommandListener) entry.getValue()).getCommandUsage(), false);
            }
            RequestBuffer.request(() -> sender.getOrCreatePMChannel().sendMessage(embedBuilder.build()));
            return CommandExitCode.SUCCESS;
        } else if (args.size() == 1){
            boolean isCommand = commands.get(args.get(0)) != null;
            if (isCommand) {
                embedBuilder.withTitle("Command help for " + args.get(0))
                        .withDescription(((CommandListener) commands.get(args.get(0))).getCommandDescription())
                        .appendField("Usage", ((CommandListener) commands.get(args.get(0))).getCommandUsage(), false);
                RequestBuffer.request(() -> sender.getOrCreatePMChannel().sendMessage(embedBuilder.build()));
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

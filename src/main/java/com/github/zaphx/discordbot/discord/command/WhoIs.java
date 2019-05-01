package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.UUIDFetcher;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.UUID;

public class WhoIs implements CommandListener {

    /**
     * This command will allow users to see who another person is ingame. This is only possible if the user has linked their accounts.
     * @param sender The command sender.
     * @param command The command used.
     * @param args The arguments provided by the sender.
     * @param destination The channel the message should be sent to. By default this is the channel the command was received in.
     * @param event The event provided by discord.
     * @return A {@link CommandExitCode} from the execution result of the command
     */
    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination, MessageReceivedEvent event) {

        if (args.size() == 1 && event.getMessage().getMentions().size() == 1) {
            IUser lookupUser = event.getMessage().getMentions().get(0);
            if (sql.isUserLinked(lookupUser.getStringID())) {
                String uuid = sql.getPlayerFromLink(lookupUser);
                String name = UUIDFetcher.getName(UUID.fromString(uuid));
                destination.sendMessage(embedManager.whoIsEmbed(lookupUser, name));
            } else {
                destination.sendMessage(embedManager.userNotLinked());
            }

            return CommandExitCode.SUCCESS;
        } else return CommandExitCode.INVALID_SYNTAX;

    }

    /**
     * Gets the command description for the help message
     * @return The command description of what the command does.
     */
    @Override
    public @NotNull String getCommandDescription() {
        return "Shows who a person is ingame, if they have linked their account.";
    }

    /**
     * Gets the command usage message for the help message
     * @return The command usage message
     */
    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "whois @<user>";
    }
}

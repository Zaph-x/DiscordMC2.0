package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.api.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.api.commandhandler.CommandListener;
import com.github.zaphx.discordbot.utilities.UUIDFetcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class WhoIs implements CommandListener {


    @Override
    public CommandExitCode onCommand(User sender, String command, List<String> args, MessageChannel destination, MessageCreateEvent event) {

        if (args.size() == 1 && event.getMessage().getUserMentions().toStream().count() == 1) {
            User lookupUser = event.getMessage().getUserMentions().toStream().findFirst().get();
            if (sql.isUserLinked(lookupUser.getId().asString())) {
                String uuid = sql.getPlayerFromLink(lookupUser.asMember(clientManager.GUILD_SNOWFLAKE).block());
                String name = UUIDFetcher.getName(UUID.fromString(uuid));
                destination.createMessage(spec -> spec.setEmbed(embedManager.whoIsEmbed(lookupUser, name))).subscribe();
            } else {
                destination.createMessage(spec -> spec.setEmbed(embedManager.userNotLinked())).subscribe();
            }

            return CommandExitCode.SUCCESS;
        } else return CommandExitCode.INVALID_SYNTAX;

    }

    @Override
    public @NotNull String getCommandDescription() {
        return "Shows who a person is ingame, if they have linked their account.";
    }

    @Override
    public @NotNull String getCommandUsage() {
        return prefix + "whois @<user>";
    }
}

package com.github.zaphx.discordbot.discord;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface Command {

    void runCommand(MessageReceivedEvent e, List<String> args);

}

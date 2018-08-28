package com.github.zaphx.discordbot.utilities;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;

public interface Loggable {

    void sendLogMessage(MessageEvent event);

}

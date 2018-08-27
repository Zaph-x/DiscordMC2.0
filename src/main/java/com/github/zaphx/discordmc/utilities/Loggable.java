package com.github.zaphx.discordmc.utilities;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;

public interface Loggable {

    void sendLogMessage(MessageEvent event);

}

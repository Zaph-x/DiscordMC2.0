package com.github.zaphx.discordbot.discord.commandhandler;

import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public interface CommandListener {

    boolean onCommand(IUser sender, String command, List<String> args, IChannel destination);

    @NotNull
    String getCommandDescription();
}

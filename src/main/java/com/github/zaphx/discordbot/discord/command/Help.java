package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.discord.commandhandler.CommandExitCode;
import com.github.zaphx.discordbot.discord.commandhandler.CommandListener;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class Help implements CommandListener {

    @Override
    public CommandExitCode onCommand(IUser sender, String command, List<String> args, IChannel destination) {


        return CommandExitCode.ERROR;
    }

    @Override
    public @NotNull String getCommandDescription() {
        return "";
    }
}

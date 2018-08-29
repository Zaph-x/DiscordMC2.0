package com.github.zaphx.discordbot.discord.command;

import com.github.zaphx.discordbot.discord.commandhandler.CommandListener;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class Help implements CommandListener {

    @Override
    public boolean onCommand(IUser sender, String command, List<String> args, IChannel destination) {


        return false;
    }

    @Override
    public @NotNull String getCommandDescription() {
        return null;
    }
}

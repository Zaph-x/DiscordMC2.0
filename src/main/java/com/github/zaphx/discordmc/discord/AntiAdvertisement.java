package com.github.zaphx.discordmc.discord;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

public class AntiAdvertisement {

    private static List<IUser> allowedUsers = new ArrayList<>();

    public AntiAdvertisement() {}

    public void checkAndHandle(MessageEvent event) {
        IMessage message = event.getMessage();
        IUser user = event.getAuthor();

    }


}

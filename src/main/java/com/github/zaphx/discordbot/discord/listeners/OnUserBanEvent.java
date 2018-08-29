package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.utilities.ChannelManager;
import com.github.zaphx.discordbot.utilities.DiscordClientManager;
import com.github.zaphx.discordbot.utilities.MessageManager;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.Permissions;

public class OnUserBanEvent {

    private ChannelManager channelManager = ChannelManager.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();

    @EventSubscriber
    public void onUserBan(UserBanEvent event) {
        if (clientManager.clientHasPermission(Permissions.VIEW_AUDIT_LOG)) {

        }
    }

}

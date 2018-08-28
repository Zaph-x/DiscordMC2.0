package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.utilities.ChannelManager;
import com.github.zaphx.discordbot.utilities.DiscordClientManager;
import com.github.zaphx.discordbot.utilities.InviteManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IUser;

public class MemberJoinEvent {

    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private ChannelManager channelManager = ChannelManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        IUser user = event.getUser();
        // TODO send embed with join and invite
        //channelManager.getChannel(DiscordChannelTypes.AUDIT_LOG).sendMessage(EMBED);
    }
}

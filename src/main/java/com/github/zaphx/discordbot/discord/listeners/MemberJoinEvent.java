package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;

public class MemberJoinEvent {

    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private ChannelManager channelManager = ChannelManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();
    private EmbedManager embedManager = EmbedManager.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();


    public MemberJoinEvent() {}


    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        messageManager.auditlog(embedManager.joinEmbed(inviteManager.getInvite(), event.getUser()));
    }
}

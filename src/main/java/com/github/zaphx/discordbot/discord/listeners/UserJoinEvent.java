package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.*;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import reactor.core.publisher.Mono;

public class UserJoinEvent {

    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private ChannelManager channelManager = ChannelManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();
    private EmbedManager embedManager = EmbedManager.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();


    public UserJoinEvent() {}

    public void onUserJoinEvent(final MemberJoinEvent event) {
        messageManager.auditlog(embedManager.joinEmbed(inviteManager.getInvite(), event.getMember()));
    }
}

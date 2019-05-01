package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;

public class MemberJoinEvent {

    /**
     * The client manager
     */
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    /**
     * The channel manager
     */
    private ChannelManager channelManager = ChannelManager.getInstance();
    /**
     * The invite manager
     */
    private InviteManager inviteManager = InviteManager.getInstance();
    /**
     * The embed manager
     */
    private EmbedManager embedManager = EmbedManager.getInstance();
    /**
     * The message manager
     */
    private MessageManager messageManager = MessageManager.getInstance();

    /**
     * The constructor
     */
    public MemberJoinEvent() {}

    /**
     * This event will handle when a user joins the discord
     * @param event The event to handle
     */
    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        messageManager.auditlog(embedManager.joinEmbed(inviteManager.getInvite(), event.getUser()));
    }
}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.InternalsManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class OnUserBanEvent {

    /**
     * The embed manager
     */
    private EmbedManager embedManager = EmbedManager.getInstance();
    /**
     * The message manager
     */
    private MessageManager messageManager = MessageManager.getInstance();
    /**
     * The client manager
     */
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    /**
     * The internals manager
     */
    private InternalsManager internalsManager = InternalsManager.getInstance();

    /**
     * The event to handle when a user is banned. This will log the ban to the log channel
     * @param event the event to handle
     */
    @EventSubscriber
    public void onUserBan(UserBanEvent event) {
        if (clientManager.clientHasPermission(Permissions.VIEW_AUDIT_LOG)) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(Dizcord.getInstance(), () -> {
                IUser banner = internalsManager.getBanner(event);
                String reason = internalsManager.getReason(event);
                messageManager.log(embedManager.banToChannel(event.getUser(),banner,reason));
            }, 40L);
        }
    }

}

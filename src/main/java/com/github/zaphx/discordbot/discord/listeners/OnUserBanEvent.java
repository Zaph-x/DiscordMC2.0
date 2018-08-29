package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.utilities.*;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class OnUserBanEvent {

    private EmbedManager embedManager = EmbedManager.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private InternalsManager internalsManager = InternalsManager.getInstance();

    @EventSubscriber
    public void onUserBan(UserBanEvent event) {
        if (clientManager.clientHasPermission(Permissions.VIEW_AUDIT_LOG)) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                IUser banner = internalsManager.getBanner(event);
                String reason = internalsManager.getReason(event);
                messageManager.log(embedManager.banToChannel(event.getUser(),banner,reason));
            }, 40L);
        }
    }

}

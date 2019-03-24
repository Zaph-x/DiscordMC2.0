package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.InternalsManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import discord4j.core.event.domain.guild.BanEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Permission;
import org.bukkit.Bukkit;
import reactor.core.publisher.Mono;

public class OnUserBanEvent {

    private EmbedManager embedManager = EmbedManager.getInstance();
    private MessageManager messageManager = MessageManager.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private InternalsManager internalsManager = InternalsManager.getInstance();

    public void onUserBan(BanEvent event) {
        if (clientManager.clientHasPermission(Permission.VIEW_AUDIT_LOG)) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(Dizcord.getInstance(), () -> {
                Member banner = internalsManager.getBanner(event);
                String reason = internalsManager.getReason(event);
                messageManager.log(embedManager.banToChannel(((Member) event.getUser()),banner,reason));
            }, 40L);
        }
    }

}

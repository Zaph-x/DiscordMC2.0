package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.managers.InviteManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class OnReadyEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();
    private SQLManager sql = SQLManager.getInstance();

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        messageManager.setMessages();
        inviteManager.update();
        messageManager.updatePeriodically();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> sql.unmute());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> sql.unmute(), 72000L, 72000L);
    }
}

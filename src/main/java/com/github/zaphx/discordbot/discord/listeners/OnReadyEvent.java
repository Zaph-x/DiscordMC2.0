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
import java.util.logging.Level;

public class OnReadyEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();
    private SQLManager sql = SQLManager.getInstance();
    private long interval = Main.getInstance().getConfig().getLong("discord.mute-check-interval",60);

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        messageManager.setMessages();
        inviteManager.update();
        messageManager.updatePeriodically();
        Main.getInstance().getLogger().log(Level.INFO, "Checking for any mutes.");
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> sql.unmute());
        Main.getInstance().getLogger().log(Level.INFO, "Setting mute check interval to " + interval + " minutes");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> sql.unmute(), 20*60L * interval, 20*60L * interval);
    }
}

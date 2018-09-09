package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.api.plugin.PluginManager;
import com.github.zaphx.discordbot.managers.InviteManager;
import com.github.zaphx.discordbot.managers.MessageManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.util.logging.Level;

public class OnReadyEvent {

    private MessageManager messageManager = MessageManager.getInstance();
    private InviteManager inviteManager = InviteManager.getInstance();
    private SQLManager sql = SQLManager.getInstance();
    private long interval = Dizcord.getInstance().getConfig().getLong("discord.mute-check-interval",60);
//    private PluginManager pluginManager = new PluginManager();

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        messageManager.setMessages();
        inviteManager.update();
        messageManager.updatePeriodically();
        Dizcord.getInstance().getLogger().log(Level.INFO, "Checking for any mutes.");
        Bukkit.getScheduler().runTaskAsynchronously(Dizcord.getInstance(), () -> sql.unmute());
        Dizcord.getInstance().getLogger().log(Level.INFO, "Setting mute check interval to " + interval + " minutes");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Dizcord.getInstance(), () -> sql.unmute(), 20*60L * interval, 20*60L * interval);
    }
}

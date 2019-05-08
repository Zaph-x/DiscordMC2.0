package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;

public class AnyEvent {

    /**
     * Debug event
     *
     * @param event Any event object
     */
    @EventSubscriber
    public void onAnyEvent(Event event) {
        if (Dizcord.getInstance().getServer().getIp().equals("")) {
            System.out.println("Event fired");
        }
    }
}

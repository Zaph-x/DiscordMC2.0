package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;

import java.lang.reflect.Field;

public class AnyEvent {

    public AnyEvent() {
        Dizcord.getInstance().getLog().info("Registering " + getClass().getSimpleName());
    }

    /**
     * Debug event
     *
     * @param event Any event object
     */
    @EventSubscriber
    public void onAnyEvent(Event event) throws ClassNotFoundException, IllegalAccessException {
        if (Dizcord.getInstance().getConfig().getBoolean("debug")) {
            System.out.println("Event fired: " + event.getClass().getName());
        }
    }
}

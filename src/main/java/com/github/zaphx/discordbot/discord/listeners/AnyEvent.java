package com.github.zaphx.discordbot.discord.listeners;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;

public class AnyEvent {

    @EventSubscriber
    public void onAnyEvent(Event event) {
        System.out.println("Event fired");
    }
}

package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.role.RoleCreateEvent;

public class OnRoleCreateEvent {

    RolesManager rolesManager = RolesManager.getInstance();

    @EventSubscriber
    public void onCreateEvent(RoleCreateEvent event) {
        rolesManager.addRole(event.getRole());
    }
}

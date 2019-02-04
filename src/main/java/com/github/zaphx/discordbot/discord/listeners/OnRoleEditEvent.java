package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;

public class OnRoleEditEvent {

    RolesManager rolesManager = RolesManager.getInstance();

    @EventSubscriber
    public void onEditEvent(RoleUpdateEvent event) {

        rolesManager.removeRole(event.getOldRole());
        rolesManager.addRole(event.getNewRole());
    }

}

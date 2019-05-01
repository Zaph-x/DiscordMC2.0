package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;

public class OnRoleEditEvent {

    /**
     * The roles manager
     */
    RolesManager rolesManager = RolesManager.getInstance();

    /**
     * The event to handle when a role is edited
     * @param event The event to handle
     */
    @EventSubscriber
    public void onEditEvent(RoleUpdateEvent event) {

        rolesManager.removeRole(event.getOldRole());
        rolesManager.addRole(event.getNewRole());
    }

}

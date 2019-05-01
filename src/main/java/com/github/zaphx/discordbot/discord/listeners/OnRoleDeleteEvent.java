package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;

public class OnRoleDeleteEvent {

    /**
     * The role manager
     */
    RolesManager rolesManager = RolesManager.getInstance();

    /**
     * The event to handle when a role is deleted
     * @param event The event to handle
     */
    @EventSubscriber
    public void onDeleteEvent(RoleDeleteEvent event) {
        rolesManager.removeRole(event.getRole());
    }

}

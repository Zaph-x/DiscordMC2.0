package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.RolesManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.role.RoleCreateEvent;

public class OnRoleCreateEvent {

    /**
     * The role manager
     */
    RolesManager rolesManager = RolesManager.getInstance();

    public OnRoleCreateEvent() {
        Dizcord.getInstance().getLog().info("Registering " + getClass().getSimpleName());
    }

    /**
     * The event to handle when a role is created
     * @param event The event to handle
     */
    @EventSubscriber
    public void onCreateEvent(RoleCreateEvent event) {
        rolesManager.addRole(event.getRole());
    }
}

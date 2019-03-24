package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import discord4j.core.event.domain.role.RoleDeleteEvent;
import reactor.core.publisher.Mono;

public class OnRoleDeleteEvent {

    RolesManager rolesManager = RolesManager.getInstance();

    public void onDeleteEvent(RoleDeleteEvent event) {
        rolesManager.removeRole(event.getRole().orElse(null));
    }

}

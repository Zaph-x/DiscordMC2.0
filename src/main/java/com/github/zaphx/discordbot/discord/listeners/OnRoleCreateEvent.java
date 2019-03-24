package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import discord4j.core.event.domain.role.RoleCreateEvent;
import reactor.core.publisher.Mono;

public class OnRoleCreateEvent {

    RolesManager rolesManager = RolesManager.getInstance();

    public void onCreateEvent(RoleCreateEvent event) {
        rolesManager.addRole(event.getRole());
    }
}

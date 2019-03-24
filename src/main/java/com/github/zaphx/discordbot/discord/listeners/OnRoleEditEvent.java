package com.github.zaphx.discordbot.discord.listeners;

import com.github.zaphx.discordbot.managers.RolesManager;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import reactor.core.publisher.Mono;

public class OnRoleEditEvent {

    RolesManager rolesManager = RolesManager.getInstance();

    public void onEditEvent(RoleUpdateEvent event) {

        rolesManager.removeRole(event.getOld().orElse(null));
        rolesManager.addRole(event.getCurrent());
    }

}

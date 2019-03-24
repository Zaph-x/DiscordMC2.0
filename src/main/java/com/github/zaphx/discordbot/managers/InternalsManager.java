package com.github.zaphx.discordbot.managers;

import discord4j.core.event.domain.guild.BanEvent;
import discord4j.core.object.audit.ActionType;
import discord4j.core.object.audit.AuditLogEntry;
import discord4j.core.object.entity.Member;

import java.util.Comparator;
import java.util.Objects;

public class InternalsManager {

    private static InternalsManager instance;

    private InternalsManager() {
    }
    /**
     * Gets the instance of the InternalsManager
     * @return A new instance if one does not exist, else the instance
     */
    public static InternalsManager getInstance() {
        return instance == null ? instance = new InternalsManager() : instance;
    }

    /**
     * Gets an audit log ban entry for the Discord Ban event
     * @param event The event to look in
     * @return The Auditlog entry of the ban
     */
    private AuditLogEntry getBanEntries(BanEvent event) {
        return event.getGuild().map(g -> g.getAuditLog(spec -> spec.setActionType(ActionType.MEMBER_BAN_ADD).setResponsibleUser(event.getUser().getId())).blockLast()).block();

                /*
                .getEntries()
                .stream()
                .sorted(Comparator.comparing(AuditLogEntry::getLongId).reversed())
                .findFirst()
                .get();*/
    }

    /**
     * Gets the banned user
     * @param event The event to look in
     * @return The user banned
     */
    public Member getBanner(BanEvent event) {
        AuditLogEntry entry = getBanEntries(event);
        return Objects.requireNonNull(event.getGuild().map(g -> g.getMemberById(entry.getResponsibleUserId())).block()).block();
    }

    /**
     * Gets the reason for a ban
     * @param event The event to look in
     * @return The reason of the ban
     */
    public String getReason(BanEvent event) {
        AuditLogEntry entry = getBanEntries(event);
        return entry.getReason().orElse("No reason was provided.");
    }
}

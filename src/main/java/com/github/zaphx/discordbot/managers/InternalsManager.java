package com.github.zaphx.discordbot.managers;

import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.entry.AuditLogEntry;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.Comparator;

public class InternalsManager {

    /**
     * The instance of the InternalsManager
     */
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
    private AuditLogEntry getBanEntries(UserBanEvent event) {
        return event.getGuild().getAuditLog(ActionType.MEMBER_BAN_ADD)
                .getEntries()
                .stream()
                .sorted(Comparator.comparing(AuditLogEntry::getLongID).reversed())
                .findFirst()
                .get();
    }

    /**
     * Gets the banned user
     * @param event The event to look in
     * @return The user banned
     */
    public IUser getBanner(UserBanEvent event) {
        AuditLogEntry entry = getBanEntries(event);
        return entry.getResponsibleUser();
    }

    /**
     * Gets the reason for a ban
     * @param event The event to look in
     * @return The reason of the ban
     */
    public String getReason(UserBanEvent event) {
        AuditLogEntry entry = getBanEntries(event);
        return entry.getReason().orElse("No reason was provided.");
    }
}

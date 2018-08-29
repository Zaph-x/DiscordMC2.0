package com.github.zaphx.discordbot.managers;

import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.entry.AuditLogEntry;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.Comparator;

public class InternalsManager {

    private static InternalsManager instance;

    private InternalsManager() {
    }

    public static InternalsManager getInstance() {
        return instance == null ? instance = new InternalsManager() : instance;
    }

    private AuditLogEntry getBanEntries(UserBanEvent event) {
        return event.getGuild().getAuditLog(ActionType.MEMBER_BAN_ADD)
                .getEntries()
                .stream()
                .sorted(Comparator.comparing(AuditLogEntry::getLongID).reversed())
                .findFirst()
                .get();
    }

    public IUser getBanner(UserBanEvent event) {
        AuditLogEntry entry = getBanEntries(event);
        return entry.getResponsibleUser();
    }

    public String getReason(UserBanEvent event) {
        AuditLogEntry entry = getBanEntries(event);
        return entry.getReason().orElse("No reason was provided.");
    }
}

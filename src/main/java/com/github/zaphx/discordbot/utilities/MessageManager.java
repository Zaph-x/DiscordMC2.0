package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Main;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.AuditLog;
import sx.blah.discord.handle.audit.entry.AuditLogEntry;
import sx.blah.discord.handle.audit.entry.DiscordObjectEntry;
import sx.blah.discord.handle.audit.entry.TargetedEntry;
import sx.blah.discord.handle.audit.entry.option.OptionKey;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.*;

public class MessageManager {

    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private IDiscordClient client = clientManager.getClient();
    private FileConfiguration config = Main.getInstance().getConfig();
    private Map<Long,IMessage> messages = new THashMap<>();
    private AuditLog log;

    private static MessageManager instance;

    private MessageManager() {
    }

    public static MessageManager getInstance() {
        return instance == null ? instance = new MessageManager() : instance;
    }

    public Object[] getDeleter(MessageDeleteEvent event) {
        AuditLog log = event.getGuild().getAuditLog(ActionType.MESSAGE_DELETE); // get the audit log of message deletes
        DiscordObjectEntry<IUser> entry = log.getDiscordObjectEntries(IUser.class).stream() // get the entries with IUser targets
                .filter(it -> it.getOptionByKey(OptionKey.CHANNEL_ID) == event.getChannel().getLongID()).max(Comparator.comparing(TargetedEntry::getTargetID).reversed()) // find the one whose channel ID matches the event's
                .orElseThrow(IllegalStateException::new); // it must exist because the event was dispatched

        IUser deleter = entry.getResponsibleUser();
        IChannel channel = event.getGuild().getChannelByID(event.getChannel().getLongID());
        return new Object[]{deleter, channel};
    }

    public Object[] test(MessageDeleteEvent event) {
        AuditLog log = event.getGuild().getAuditLog(ActionType.MESSAGE_DELETE); // get the audit log of message deletes
        AuditLogEntry entry = event.getGuild().getAuditLog(ActionType.MESSAGE_DELETE)
                .getEntries()
                .stream()
                .sorted(Comparator.comparing(AuditLogEntry::getLongID).reversed())
                .findFirst()
                .get();
        return new Object[]{entry.getResponsibleUser(), event.getChannel()};
    }

    public void setDeleteLog() {
        log = client.getGuildByID(config.getLong("discord.guild-id")).getAuditLog(ActionType.MESSAGE_DELETE); // get the audit log of message deletes
    }

    public void log(String message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.LOG.getID()).sendMessage(message));
    }

    public void log(EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.LOG.getID()).sendMessage(message));
    }

    public void auditlog(String message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.AUDIT_LOG.getID()).sendMessage(message));
    }

    public void auditlog(EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.AUDIT_LOG.getID()).sendMessage(message));
    }

    public void setMessages() {
        client.getGuildByID(config.getLong("discord.guild-id")).getChannels().forEach(channel -> channel.getMessageHistory(200).forEach(message -> messages.put(message.getLongID(),message)));
    }

    public IMessage getMessageFromLog(long id) {
        return messages.get(id);
    }

    public void destroyMessages() {
        messages.clear();
    }
}

package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.AuditLog;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

public class MessageManager {

    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private IDiscordClient client = clientManager.getClient();
    private FileConfiguration config = Dizcord.getInstance().getConfig();
    private TMap<Long, IMessage> messages = new THashMap<>();
    private AuditLog log;

    private static MessageManager instance;
    private EmbedManager embedManager;

    private MessageManager() {
    }

    public static MessageManager getInstance() {
        return instance == null ? instance = new MessageManager() : instance;
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
        client.getGuildByID(config.getLong("discord.guild-id")).getChannels().forEach(channel -> channel.getMessageHistory(200).forEach(message -> messages.put(message.getLongID(), message)));
    }

    public IMessage getMessageFromLog(long id) {
        return messages.get(id);
    }

    public void destroyMessages() {
        messages.clear();
    }

    public void updatePeriodically() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Dizcord.getInstance(), () -> {
            destroyMessages();
            setMessages();
        }, 1200, 1200);
    }

    public void addMessage(IMessage message) {
        messages.put(message.getLongID(), message);
    }
}

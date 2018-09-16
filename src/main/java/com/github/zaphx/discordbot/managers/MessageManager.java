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
    /**
     * Gets the instance of the MessageManager
     * @return A new instance if one does not exist, else the instance
     */
    public static MessageManager getInstance() {
        return instance == null ? instance = new MessageManager() : instance;
    }

    /**
     * Sets the auditlog
     */
    public void setDeleteLog() {
        log = client.getGuildByID(config.getLong("discord.guild-id")).getAuditLog(ActionType.MESSAGE_DELETE); // get the audit log of message deletes
    }

    /**
     * Sends a message to the log channel set in the config
     * @param message The message to send
     */
    public void log(String message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.LOG.getID()).sendMessage(message));
    }

    /**
     * Sends a message to the log channel set in the config
     * @param message The message to send
     */
    public void log(EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.LOG.getID()).sendMessage(message));
    }

    /**
     * Sends a message to the audit log channel set in the config
     * @param message The message to send
     */
    public void auditlog(String message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.AUDIT_LOG.getID()).sendMessage(message));
    }

    /**
     * Sends a message to the audit log channel set in the config
     * @param message The message to send
     */
    public void auditlog(EmbedObject message) {
        RequestBuffer.request(() -> client.getChannelByID(DiscordChannelTypes.AUDIT_LOG.getID()).sendMessage(message));
    }

    /**
     * 200 messages from every channel. This is to retrieve them when a message is deleted
     */
    public void setMessages() {
        client.getGuildByID(config.getLong("discord.guild-id")).getChannels().forEach(channel -> channel.getMessageHistory(200).forEach(message -> messages.put(message.getLongID(), message)));
    }

    /**
     * Gets a message from the internal message cache
     * @param id The ID of the message to look for
     * @return The message specified by ID
     */
    public IMessage getMessageFromLog(long id) {
        return messages.get(id);
    }

    /**
     * Clears the message cache
     */
    public void destroyMessages() {
        messages.clear();
    }

    /**
     * Called once, to set a timer to update message cache
     */
    public void updatePeriodically() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Dizcord.getInstance(), () -> {
            destroyMessages();
            setMessages();
        }, 1200, 1200);
    }

    /**
     * Adds a message to the message cache
     * @param message The message to add
     */
    public void addMessage(IMessage message) {
        messages.put(message.getLongID(), message);
    }
}

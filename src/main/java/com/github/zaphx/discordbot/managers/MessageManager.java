package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.AuditLog;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MessageManager {

    /**
     * A map of every player hash
     */
    public TMap<Integer, Player> hashes = new THashMap<>();
    /**
     * A map of every discord user hash
     */
    public TMap<Integer, IUser> discord = new THashMap<>();
    /**
     * The client manager
     */
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    /**
     * The discord client object
     */
    private IDiscordClient client = clientManager.getClient();
    /**
     * The configuration file
     */
    private FileConfiguration config = Dizcord.getInstance().getConfig();
    /**
     * A map of every message and its Id
     */
    private TMap<Long, IMessage> messages = new THashMap<>();
    /**
     * The audit log
     */
    private AuditLog log;
    /**
     * The SQL manager
     */
    private SQLManager sqlManager = SQLManager.getInstance();
    /**
     * The message manager
     */
    private static MessageManager instance;
    /**
     * The embed manager
     */
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
        for (IChannel channel : client.getGuildByID(config.getLong("discord.guild-id")).getChannels()) {
            for (IMessage message : channel.getMessageHistory(200)) {
                sqlManager.executeStatementAndPost("INSERT INTO " + sqlManager.prefix + "messages (id, content, author, author_name, channel) VALUES ('%s','%s','%s','%s','%s') \nON DUPLICATE KEY UPDATE content = '%s'",
                        message.getStringID(),
                        message.getContent().replaceAll("'", "¼"),
                        message.getAuthor().getStringID(),
                        message.getAuthor().getName(),
                        message.getChannel().getStringID(),
                        message.getContent().replaceAll("'", "¼"));
            }
        }
    }

    /**
     * Gets a deleted message and the information associated with it
     * @param id The id of the message to look for
     * @return A THashMap with the information associated with the message
     */
    public THashMap<String, String> getDeletedMessage(String id) {
        return sqlManager.getDeletedMessage(id);
    }

    /**
     * Adds a message to the SQL database
     * @param message the message to add
     */
    public void addMessage(IMessage message) {
        sqlManager.addMessage(message);
    }
}

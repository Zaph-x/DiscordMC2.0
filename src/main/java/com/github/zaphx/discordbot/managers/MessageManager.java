package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import discord4j.core.DiscordClient;
import discord4j.core.object.audit.ActionType;
import discord4j.core.object.entity.Message;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import discord4j.core.object.entity.Member;

public class MessageManager {

    public TMap<Integer, Player> hashes = new THashMap<>();
    public TMap<Integer, Member> discord = new THashMap<>();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private DiscordClient client = clientManager.getClient();
    private FileConfiguration config = Dizcord.getInstance().getConfig();
    private TMap<Long, Message> messages = new THashMap<>();
    private AuditLog log;
    private SQLManager sqlManager = SQLManager.getInstance();

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
    public void addMessage(Message message) {
        sqlManager.addMessage(message);
    }
}

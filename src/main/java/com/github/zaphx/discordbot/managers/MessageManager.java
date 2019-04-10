package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import discord4j.core.DiscordClient;
import discord4j.core.object.audit.ActionType;
import discord4j.core.object.audit.AuditLogEntry;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MessageManager {

    public TMap<Integer, Player> hashes = new THashMap<>();
    public TMap<Integer, Member> discord = new THashMap<>();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private DiscordClient client = clientManager.getClient();
    private FileConfiguration config = Dizcord.getInstance().getConfig();
    private TMap<Long, Message> messages = new THashMap<>();
    private List<AuditLogEntry> log;
    private SQLManager sqlManager = SQLManager.getInstance();

    private static MessageManager instance;
    private EmbedManager embedManager;

    private MessageManager() {
    }

    /**
     * Gets the instance of the MessageManager
     *
     * @return A new instance if one does not exist, else the instance
     */
    public static MessageManager getInstance() {
        return instance == null ? instance = new MessageManager() : instance;
    }

    /**
     * Sets the auditlog
     */
    public void setDeleteLog() {
        client.getGuildById(clientManager.GUILD_SNOWFLAKE).map(guild -> guild.getAuditLog().filter(auditLogEntry -> ActionType.MESSAGE_DELETE.getValue() == auditLogEntry.getActionType().getValue())).subscribe(entries -> log.addAll(Objects.requireNonNull(entries.collectList().block()))); //(ActionType.MESSAGE_DELETE); // get the audit log of message deletes
    }

    /**
     * Sends a message to the log channel set in the config
     *
     * @param message The message to send
     */
    public void log(String message) {
        client.getChannelById(DiscordChannelTypes.LOG.getId()).cast(MessageChannel.class)
                .flatMap(messageChannel -> messageChannel.createMessage(message)).subscribe();
    }

    /**
     * Sends a message to the log channel set in the config
     *
     * @param embed The message to send
     */
    public void log(Consumer<EmbedCreateSpec> embed) {
        client.getChannelById(DiscordChannelTypes.LOG.getId())
                .cast(MessageChannel.class)
                .flatMap(messageChannel -> messageChannel.createMessage(messageCreateSpec -> {
                    messageCreateSpec.setEmbed(embed);
                })).subscribe();
    }

    /**
     * Sends a message to the audit log channel set in the config
     *
     * @param message The message to send
     */
    public void auditlog(String message) {
        client.getChannelById(DiscordChannelTypes.AUDIT_LOG.getId())
                .cast(MessageChannel.class)
                .flatMap(messageChannel -> messageChannel.createMessage(message)).subscribe();
    }

    /**
     * Sends a message to the audit log channel set in the config
     *
     * @param embed The message to send
     */
    public void auditlog(Consumer<EmbedCreateSpec> embed) {
        client.getChannelById(DiscordChannelTypes.AUDIT_LOG.getId()).cast(MessageChannel.class)
                .flatMap(messageChannel -> messageChannel.createMessage(messageCreateSpec -> {
                    messageCreateSpec.setEmbed(embed);
                })).subscribe();
    }

    /**
     * 200 messages from every channel. This is to retrieve them when a message is deleted
     */
    public void setMessages() {
        for (TextChannel channel : Objects.requireNonNull(client.getGuildById(clientManager.GUILD_SNOWFLAKE)
                .flatMap(guild -> guild.getChannels()
                        .filter(guildChannel -> guildChannel instanceof TextChannel)
                        .cast(TextChannel.class)
                        .collectList()).block())) {
            for (Message message : Objects.requireNonNull(channel.getMessagesBefore(Snowflake.of(Instant.now())).take(200).collectList().block())) {
                sqlManager.executeStatementAndPost("INSERT INTO " + sqlManager.prefix + "messages (id, content, author, author_name, channel) VALUES ('%s','%s','%s','%s','%s') \nON DUPLICATE KEY UPDATE content = '%s'",
                        message.getId().asString(),
                        message.getContent().orElse("").replaceAll("'", "¼"),
                        message.getAuthor().map(a-> a.getId().asString()).get(),
                        message.getAuthor().map(User::getUsername).get(),
                        Objects.requireNonNull(message.getChannel().block()).getId().asString(),
                        message.getContent().orElse("").replaceAll("'", "¼"));
            }
        }
    }

    /**
     * Gets a deleted message and the information associated with it
     *
     * @param id The id of the message to look for
     * @return A THashMap with the information associated with the message
     */
    public THashMap<String, String> getDeletedMessage(String id) {
        return sqlManager.getDeletedMessage(id);
    }

    /**
     * Adds a message to the SQL database
     *
     * @param message the message to add
     */
    public void addMessage(Message message) {
        sqlManager.addMessage(message);
    }
}

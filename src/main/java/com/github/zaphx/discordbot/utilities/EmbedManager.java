package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Main;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.Instant;

public class EmbedManager {

    private MessageManager mm = MessageManager.getInstance();
    private static EmbedManager instance;
    private SQLManager sql = SQLManager.getInstance();
    private Main main = Main.getInstance();
    private FileConfiguration config = main.getConfig();
    private String prefix = config.getString("sql.prefix");

    private EmbedManager() {
    }

    public static EmbedManager getInstance() {
        return instance == null ? instance = new EmbedManager() : instance;
    }

    public EmbedObject warningToUser(IUser warned, IUser warnee, String reason, IGuild guild) {
        sql.executeStatementAndPost("INSERT INTO " + prefix + "%s (id, reason, warnee) VALUES ('%s','%s','%s')", "warnings", warned.getStringID(), reason, warned.getName());
        long ticketID = sql.countTickets("warnings");
        IChannel rulesChan = guild.getChannelByID(DiscordChannelTypes.RULES.getID());
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Warning**__")
                .withDesc("You have been warned for: " + reason + ". " +
                        "\nYour ticket ID is: **" + Long.toString(ticketID) + "**. " +
                        "\nYou were warned by: **" + warnee.getName() + "#" + warnee.getDiscriminator() + "**. " +
                        "\nPlease make sure you have read the rules in " + rulesChan.mention() + "." +
                        "\nIf you think this is a mistake, please report it to the owner of the server, with a screenshot of this message.");
        eb.withColor(new Color(133, 150, 211))
                .withFooterText(warned.getName())
                .withFooterIcon(warned.getAvatarURL())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public EmbedObject warningToChannel(IUser warned, IUser warnee, String reason, IGuild guild) {
        long ticketID = sql.countTickets("warnings");
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Warning**__")
                .withDesc("**User warned:** " + warned.getName() + "#" + warned.getDiscriminator() +
                        "\n**Ticket ID:** " + Long.toString(ticketID) + ". " +
                        "\n**User ID:** " + warned.getStringID() +
                        "\n**Warned by:** " + warnee.getName() + "#" + warnee.getDiscriminator() + ". " +
                        "\n**Reason:** " + reason + ".")
                .withColor(new Color(133, 150, 211))
                .withFooterText(warned.getName())
                .withFooterIcon(warned.getAvatarURL())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public EmbedObject joinEmbed(IExtendedInvite invite, IUser joined) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTimestamp(Instant.now())
                .withTitle("**A user joined the guild**")
                .withThumbnail(joined.getAvatarURL())
                .appendField("User:", joined.mention(), false)
                .appendField("With invite", invite.getCode(), true)
                .appendField("Invite by user", invite.getInviter().mention(), true)
                .withColor(new Color(120, 193, 82));

        return eb.build();
    }

    public EmbedObject messageDeleteEmbed(IUser author, IChannel channel, IMessage message) {
        EmbedBuilder builder = new EmbedBuilder();
        String content = message.getFormattedContent();
        if (content.isEmpty()) {
            content = "Embed";
        }
        builder.withTimestamp(Instant.now())
                .withTitle("**A message was deleted**")
                .appendField("Author", author.mention(),true)
                //.appendField("Deleter", deleter.mention(), true)
                .appendField("Channel", channel.mention(),true)
                .appendField("Message id", message.getStringID(), true)
                .appendField("Message content", content, false)
                .withThumbnail(author.getAvatarURL())
                .withColor(new Color(242, 56, 79));
        return builder.build();
    }

    public EmbedObject banToChannel(IUser banned, IUser bannee, String reason) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("**A user was banned**")
                .withColor(new Color(133, 150, 211))
                .withTimestamp(Instant.now())
                .withThumbnail(banned.getAvatarURL())
                .appendField("User banned", banned.mention(),true)
                .appendField("User banning", bannee.mention(), true)
                .appendField("With reason",reason,false);
        return eb.build();
    }
}

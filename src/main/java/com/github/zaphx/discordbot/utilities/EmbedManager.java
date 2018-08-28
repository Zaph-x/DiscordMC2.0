package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Main;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.Instant;

public class EmbedManager {

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
        eb.withColor(new Color(145, 145, 145))
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
                .withColor(new Color(145, 145, 145))
                .withFooterText(warned.getName())
                .withFooterIcon(warned.getAvatarURL())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }
}

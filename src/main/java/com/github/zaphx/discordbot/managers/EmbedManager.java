package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.List;

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
                .appendField("Author", author.mention(), true)
                //.appendField("Deleter", deleter.mention(), true)
                .appendField("Channel", channel.mention(), true)
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
                .appendField("User banned", banned.mention(), true)
                .appendField("User banning", bannee.mention(), true)
                .appendField("With reason", reason, false);
        return eb.build();
    }

    public EmbedObject reportAdvertisementEmbed(MessageEvent e) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withTitle("**A user tried to advertise**")
                .withTimestamp(Instant.now())
                .appendField("User advertising", e.getAuthor().mention(), false)
                .appendField("Original message", e.getMessage().getFormattedContent(), false)
                .withFooterIcon(e.getAuthor().getAvatarURL());
        return builder.build();
    }

    /*
     * Sends a message to a user regarding proper report criteria.
     * Method used in checkValidity()
     */
    public EmbedObject incorrectReportEmbed(List<String> strings, IMessage message) {
        StringBuilder stringBuilder = new StringBuilder();
        strings.forEach(s -> stringBuilder.append("- ").append(s).append("\n"));
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withAuthorName("Incorrect Format")
                .withTimestamp(Instant.now())
                .withTitle("You've formatted your issue report incorrectly!")
                .withDescription("Please structure your issue report WITH THE FOLLOWING FIELDS (feel free to copy and paste) - \nTitle of Issue: \nMC or Discord Related: \nMC Username: \nWorld/Channel: \nDescription:")
                .withFooterText("Your MC username lets us know who you are in-game, even if your issue is not MC related. Thank you!")
                .appendField("You forgot the following in your report:", stringBuilder.toString(), false)
                .appendField("Your previous message was:", message.getFormattedContent(), false)
                .build();
    }

    /*
     * Sends a message to a user informing them of a successful report.
     * Method used in officiallyFileReport()
     */
    public EmbedObject correctReportEmbed(IMessage message) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withAuthorName("Issue report submitted successfully!")
                .withTimestamp(Instant.now())
                .withTitle("We have received your issue report and it has been filed away!")
                .withDescription("Thank you for reporting the issue. Please contact a staff member if this is an URGENT issue, such as players fighting or an extremely game-breaking bug.\n " +
                        "It is NOT URGENT if this is just something small being broken or a feature request. Thank you for your cooperation!")
                .appendField("Your suggestion was:", message.getFormattedContent(), false)
                .build();
    }

    /*
     * Sends a message to a user regarding proper report criteria.
     * Method used in checkValidity()
     */
    public EmbedObject incorrectSuggestionEmbed(List<String> strings, IMessage message) {
        StringBuilder stringBuilder = new StringBuilder();
        strings.forEach(s -> stringBuilder.append("- ").append(s).append("\n"));
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withAuthorName("Incorrect Format")
                .withTimestamp(Instant.now())
                .withTitle("You've formatted your issue report incorrectly!")
                .withDescription("Please structure your suggestion WITH THE FOLLOWING FIELDS (feel free to copy and paste) - \nSuggestion name: \nMC or Discord: \nDescription:")
                .withFooterText("Your MC username lets us know who you are in-game, even if your issue is not MC related. Thank you!")
                .appendField("You forgot the following in your report:", stringBuilder.toString(), false)
                .appendField("Your previous message was:", message.getFormattedContent(), false)
                .build();
    }

    /*
     * Sends a message to a user informing them of a successful report.
     * Method used in officiallyFileReport()
     */
    public EmbedObject correctSuggestionEmbed(IMessage message) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withAuthorName("Issue report submitted successfully!")
                .withTimestamp(Instant.now())
                .withTitle("We have received your suggestion and it has been filed away!")
                .withDescription("Thank you for suggesting new features. Your suggestions are what keep us going as a community, and they are therefore greatly appreciated.\n " +
                        "Please note that we are not doing this full time, so your suggestion might take a while to be reviewed.")
                .appendField("Your suggestion was:", message.getFormattedContent(), false)
                .build();
    }
}

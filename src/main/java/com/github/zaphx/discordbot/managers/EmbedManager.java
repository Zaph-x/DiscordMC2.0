package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import gnu.trove.map.hash.THashMap;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class EmbedManager {

    /**
     * The message manager
     */
    private MessageManager mm = MessageManager.getInstance();
    /**
     * The instance of the EmbedManager
     */
    private static EmbedManager instance;
    /**
     * The SQL manager
     */
    private SQLManager sql = SQLManager.getInstance();
    /**
     * The discord client object
     */
    private Dizcord dizcord = Dizcord.getInstance();
    /**
     * The discord config file
     */
    private FileConfiguration config = dizcord.getConfig();
    /**
     * The prefix for the sql database
     */
    private String prefix = config.getString("sql.prefix");
    /**
     * The client manager
     */
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();

    /**
     * The default constructor of the embed manager
     */
    private EmbedManager() {
    }

    /**
     * A getter method to get the instance of the embed manager
     * @return The instance of the embed manager
     */
    public static EmbedManager getInstance() {
        return instance == null ? instance = new EmbedManager() : instance;
    }

    /**
     * This method will send a warning to the warned user
     * @param warned The warned user
     * @param warnee The person warning
     * @param reason The reason for the warn
     * @param guild The guild to warn in
     * @return An {@link EmbedObject} with a warning in it
     */
    public EmbedObject warningToUser(IUser warned, IUser warnee, String reason, IGuild guild) {
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

    /**
     * This method will log a warning in the log channel
     * @param warned The warned user
     * @param warnee The user warning
     * @param reason The reason for the warn
     * @param guild The guild to warn in
     * @return An {@link EmbedObject} with a warning in it
     */
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

    /**
     * This method will let the command sender know they sent an invalid command
     * @return An {@link EmbedObject} lettin the sender know the command was invalid
     */
    public EmbedObject invalidCommandEmbed() {
        return new EmbedBuilder()
                .withTitle("Invalid command")
                .withDesc("There is no command with that name! See the help list for all valid commands.")
                .withColor(new Color(242, 56, 79))
                .withTimestamp(Instant.now())
                .build();
    }

    /**
     * This method will let the sender know they used the wrong syntax for their command
     * @param command The command the user was trying to execute
     * @return An {@link EmbedObject} telling the user they messed up the syntax
     */
    public EmbedObject invalidSyntaxEmbed(String command) {
        return new EmbedBuilder()
                .withTitle("Invalid command format")
                .withDesc("The command was not formatted right! See `ob!help " + command + "` for correct usage.")
                .withColor(new Color(242, 56, 79))
                .withTimestamp(Instant.now())
                .build();
    }

    /**
     * This method will let the sender know they don't have permissions to perform the command
     * @return An {@link EmbedObject} letting the sender know they don't have the required permissions
     */
    public EmbedObject insufficientPermissions() {
        return new EmbedBuilder()
                .withTitle("Insufficient permissions")
                .withDesc("You are lacking one or more permissions to perform this command! Sorry.")
                .withColor(new Color(242, 56, 79))
                .withTimestamp(Instant.now())
                .build();
    }

    /**
     * This method will notify the sender of a command that the client does not have the required permissions to perform an action
     * @return An {@link EmbedObject} letting the sender know the client doesn't have the required permissions
     */
    public EmbedObject insufficientClientPermissions() {
        return new EmbedBuilder()
                .withTitle("Insufficient permissions")
                .withDesc("The bot is missing one or more permissions to perform that command! Please contact the server administrator.")
                .withColor(new Color(242, 56, 79))
                .withTimestamp(Instant.now())
                .build();
    }

    /**
     * This method will let the sender of a message know they swore
     * @return An {@link EmbedObject} letting the sender know they swore
     */
    public EmbedObject swearEmbed() {
        return new EmbedBuilder()
                .withTitle("Oopsie. That's a no-go!")
                .withDesc("We like to keep a nice tone here. Please do not swear, thank you.")
                .withColor(new Color(242, 56, 79))
                .withTimestamp(Instant.now())
                .build();
    }

    /**
     * This method will send an embed to the log channel, letting the staff know a new person joined the guild. The embed will also say who invited the user as well as what invite was used
     * @param invite The invite used to join the guild
     * @param joined The user joining the guild
     * @return An {@link EmbedObject} letting staff know a new person joined the guild
     */
    public EmbedObject joinEmbed(IExtendedInvite invite, IUser joined) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTimestamp(Instant.now())
                .withTitle("**A user joined the guild**")
                .withThumbnail(joined.getAvatarURL())
                .appendField("User:", joined.mention() + " (" + joined.getName() + ")", false)
                .appendField("With invite", invite.getCode(), true)
                .appendField("Invite by user", invite.getInviter().mention(), true)
                .withColor(new Color(120, 193, 82));

        return eb.build();
    }

    /**
     * This method will send an embed to the log channel, with a message that was just deleted
     * @param message The hashmap containing the message information
     * @return An {@link EmbedObject} with information on the deleted message
     */
    public EmbedObject messageDeleteEmbed(THashMap message) {
        EmbedBuilder builder = new EmbedBuilder();
        String content = ((String) message.get("content")).replaceAll("¼", "'");
        IUser author = clientManager.getClient().getUserByID(Long.parseLong((String) message.get("author")));
        IChannel channel = clientManager.getClient().getChannelByID(Long.parseLong((String) message.get("channel")));
        String ID = (String) message.get("id");
        if (content.isEmpty()) {
            content = "Embed";
        }
        builder.withTimestamp(Instant.now())
                .withTitle("**A message was deleted**")
                .appendField("Author", author.mention() + " (" + author.getName() + ")", true)
                //.appendField("Deleter", deleter.mention(), true)
                .appendField("Channel", channel.mention(), true)
                .appendField("Message id", ID, true)
                .appendField("Message content", content, false)
                .withThumbnail(author.getAvatarURL())
                .withColor(new Color(242, 56, 79));
        return builder.build();
    }

    /**
     * This method will log a ban to the log channel
     * @param banned The user banned
     * @param bannee The user banning
     * @param reason The reason for the ban
     * @return An {@link EmbedObject} containing information on the ban
     */
    public EmbedObject banToChannel(IUser banned, IUser bannee, String reason) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("**A user was banned**")
                .withColor(new Color(133, 150, 211))
                .withTimestamp(Instant.now())
                .withThumbnail(banned.getAvatarURL())
                .appendField("User banned", banned.mention() + " (" + banned.getName() + ")", true)
                .appendField("User banning", bannee.mention(), true)
                .appendField("With reason", reason, false);
        return eb.build();
    }

    /**
     * This method will return an embed containing information that saomeone advertised
     * @param e The message event
     * @return An {@link EmbedObject} containing information if the advertisement
     */
    public EmbedObject reportAdvertisementEmbed(MessageEvent e) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withTitle("**A user tried to advertise**")
                .withTimestamp(Instant.now())
                .appendField("User advertising", e.getAuthor().mention() + " (" + e.getAuthor().getName() + ")", false)
                .appendField("Original message", e.getMessage().getFormattedContent(), false)
                .withFooterIcon(e.getAuthor().getAvatarURL());
        return builder.build();
    }

    /**
     * This method will return an embed stating that a report was incorrectly formatted
     * @param strings The fields that were incorrectly formatted
     * @param message The report that was made
     * @return An {@link EmbedObject} containing information on the incorrect report
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

    /**
     * This method will return an embed stating that the report was formatted correct
     * @param message The report that was made
     * @return An {@link EmbedObject} stating the correct formatting
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

    /**
     * This method will return an embed notifying the sender that they could not link their account to the specified player
     * @param name The name of the player
     * @return An {@link EmbedObject} with an error message
     */
    public EmbedObject noPlayerEmbed(String name) {
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withAuthorName("That player is not present!")
                .withTimestamp(Instant.now())
                .withTitle("The player, " + name + ", does not seem to be online at the moment.").build();
    }

    public EmbedObject userAlreadyLinked() {
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withDescription("It seems that account has already been linked!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject userNotLinked() {
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withDescription("It seems that account has not been linked!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject selfNotLinked() {
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withDescription("It seems your account has not been linked!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject userUnlinked() {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription("The user was successfully unlinked!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject selfUnlinked() {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription("Your account was successfully unlinked!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject whoIsEmbed(IUser user, String name) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription("The account linked to user, " + user.getName() + ", is " + name)
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject roleJoinEmbed(String name) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription("You joined the " + name + " role!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject roleLeaveEmbed(String name) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription("You left the " + name + " role!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject roleAddedToEveryone(String name) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription("Everyone got the " + name + " role!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject roleMentionableChanged(String name, boolean changed) {
        return new EmbedBuilder().withColor(new Color(120, 193, 82))
                .withDescription(name + " role had mentionable changed to " + changed)
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject roleAlreadyActive(String name) {
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withDescription("You already have the " + name + " role!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject roleNotActive(String name) {
        return new EmbedBuilder().withColor(new Color(242, 56, 79))
                .withDescription("You don't have the " + name + " role!")
                .withTimestamp(Instant.now()).build();
    }

    public EmbedObject userLinked(IUser user, String username) {
        return new EmbedBuilder().withColor(new Color(133, 150, 211))
                .withTitle("A user linked their account")
                .appendField("Discord name", user.mention(), true)
                .appendField("Minecraft name", username, true)
                .appendField("Discord ID", user.getStringID(), false)
                .withTimestamp(Instant.now()).build();

    }

    public EmbedObject userUnLinked(IUser user, String username) {
        return new EmbedBuilder().withColor(new Color(133, 150, 211))
                .withTitle("A user unlinked their account")
                .appendField("Discord name", user.mention(), true)
                .appendField("Minecraft name", username, true)
                .appendField("Discord ID", user.getStringID(), false)
                .withTimestamp(Instant.now()).build();
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

    public EmbedObject muteEmbed(String reason, String time, IUser muter) {
        return new EmbedBuilder().withColor(new Color(133, 150, 211))
                .withTimestamp(Instant.now())
                .withTitle("You have been muted!")
                .appendField("Muted by", muter.mention(), true)
                .appendField("Muted for", time, true)
                .appendField("Reason", reason, false).build();
    }

    public EmbedObject logMuteEmbed(String reason, String time, IUser muter, IUser muted) {
        return new EmbedBuilder().withColor(new Color(133, 150, 211))
                .withTimestamp(Instant.now())
                .withTitle("A user has been muted!")
                .appendField("Muted", muted.mention(), true)
                .appendField("Muted by", muter.mention(), true)
                .appendField("Muted for", time, true)
                .appendField("Reason", reason, false).build();
    }

    public EmbedObject exception() {
        return new EmbedBuilder()
                .withDescription("The bot encountered an error and it has been reported!")
                .withColor(new Color(242, 56, 79))
                .build();
    }

    public EmbedObject exceptionToOwner(String type, String stacktrace) {
        return new EmbedBuilder()
                .withDescription("The bot encountered an error and it has been reported!")
                .appendField(type, stacktrace.substring(0, 1024), false)
                .withColor(new Color(242, 56, 79))
                .build();
    }
}

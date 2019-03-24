package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.utilities.ArgumentException;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.ExtendedInvite;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Image;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import gnu.trove.map.hash.THashMap;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

public class EmbedManager {

    private static EmbedManager instance;
    private final SQLManager sql = SQLManager.getInstance();
    private final Dizcord dizcord = Dizcord.getInstance();
    private final DiscordClientManager clientManager = DiscordClientManager.getInstance();
    private final Color WARNING = new Color(242, 56, 79);
    private final Color NEUTRAL = new Color(133, 150, 211);
    private final Color SUCCESS = new Color(120, 193, 82);

    private EmbedManager() {
    }

    public static EmbedManager getInstance() {
        return instance == null ? instance = new EmbedManager() : instance;
    }

    public Consumer<EmbedCreateSpec> warningToUser(User warned, User warnee, String reason, Mono<Guild> guild) {
        long tickedId = sql.countTickets("warnings");
        TextChannel rulesChannel = guild.block().getChannelById(DiscordChannelTypes.RULES.getId()).cast(TextChannel.class).block();

        return embedCreateSpec -> embedCreateSpec.setTitle("__**Warning**__")
                .setDescription("You have been warned for " + reason + "." +
                        "\nYour ticket Id is: **" + tickedId + "**." +
                        "\nYou were warned by: **" + warnee.getUsername() + "**" +
                        "\nPlease make sure you have read the rules in " + rulesChannel.getMention() + "." +
                        "\nIf you believe this is a mistake, please report it to the owner of the server, with a screenshot of this message.")
                .setColor(WARNING)
                .setFooter(warned.getUsername(), warned.getAvatarUrl())
                .setTimestamp(Instant.now())
                .setAuthor(guild.block().getName(), null, guild.block().getIconUrl(Image.Format.JPEG).orElseThrow(IllegalArgumentException::new));
    }

    public Consumer<EmbedCreateSpec> warningToChannel(User warned, User warnee, String reason, Guild guild, MessageChannel destination) {
        long ticketId = sql.countTickets("warnings");

        return embedCreateSpec -> embedCreateSpec.setTitle("__**Warning**__")
                .setDescription("**User warned:** " + warned.getUsername() + "#" + warned.getDiscriminator() +
                        "\n**Ticket Id:** " + ticketId + ". " +
                        "\n**User Id:** " + warned.getId().asString() +
                        "\n**Warned by:** " + warnee.getUsername() + "#" + warnee.getDiscriminator() + ". " +
                        "\n**Reason:** " + reason + ".")
                .setColor(NEUTRAL)
                .setFooter(warned.getUsername(), warned.getAvatarUrl())
                .setTimestamp(Instant.now())
                .setAuthor(guild.getName(), null, guild.getIconUrl(Image.Format.JPEG).orElseThrow(IllegalArgumentException::new));
    }

    public Consumer<EmbedCreateSpec> invalidCommandEmbed() {

        return embedCreateSpec -> embedCreateSpec.setTitle("Invalid command")
                .setDescription("There is no command with that name! See the help list for all valid commands.")
                .setColor(WARNING)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> invalidSyntaxEmbed(String command) {


        return embedCreateSpec -> embedCreateSpec.setTitle("Invalid command format")
                .setDescription("The command was not formatted right! See `ob!help " + command + "` for correct usage.")
                .setColor(WARNING)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> insufficientPermissions() {

        return embedCreateSpec -> embedCreateSpec
                .setTitle("Insufficient permissions")
                .setDescription("You are lacking one or more permissions to perform this command! Sorry.")
                .setColor(WARNING)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> insufficientClientPermissions() {

        return embedCreateSpec -> embedCreateSpec
                .setTitle("Insufficient permissions")
                .setDescription("The bot is missing one or more permissions to perform that command! Please contact the server administrator.")
                .setColor(WARNING)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> swearEmbed() {

        return embedCreateSpec -> embedCreateSpec
                .setTitle("Oopsie. That's a no-go!")
                .setDescription("We like to keep a nice tone here. Please do not swear, thank you.")
                .setColor(WARNING)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> joinEmbed(ExtendedInvite invite, Member joined) {

        return embedCreateSpec -> embedCreateSpec
                .setTimestamp(Instant.now())
                .setTitle("**A user joined the guild**")
                .setThumbnail(joined.getAvatarUrl())
                .addField("User:", joined.getDisplayName() + " (" + joined.getUsername() + ")", false)
                .addField("With invite", invite.getCode(), true)
                .addField("Invite by user", invite.getInviter().map(User::getMention).blockOptional().orElseThrow(NoSuchFieldError::new), true)
                .setColor(NEUTRAL);
    }

    public Consumer<EmbedCreateSpec> messageDeleteEmbed(THashMap<String, String> message) {
        String content = message.get("content").replaceAll("¼", "'");
        Mono<User> author = clientManager.getClient().getUserById(Snowflake.of(Long.parseLong(message.get("author"))));
        Mono<TextChannel> targetChannel = clientManager.getClient().getChannelById(Snowflake.of(Long.parseLong(message.get("channel")))).cast(TextChannel.class);
        String Id = message.get("id");
        if (content.isEmpty()) {
            content = "Embed";
        }
        String finalContent = content;
        return embedCreateSpec -> embedCreateSpec
                .setTimestamp(Instant.now())
                .setTitle("**A message was deleted**")
                .addField("Author", author.map(User::getMention) + " (" + author.map(User::getUsername) + ")", true)
                //.addField("Deleter", deleter.getMention(), true)
                .addField("Channel", targetChannel.map(TextChannel::getMention).blockOptional().orElseThrow(ArgumentException::new), true)
                .addField("Message id", Id, true)
                .addField("Message content", finalContent, false)
                .setThumbnail(author.map(User::getAvatarUrl).blockOptional().orElseThrow(IllegalArgumentException::new))
                .setColor(WARNING);
    }

    public Consumer<EmbedCreateSpec> banToChannel(Member banned, Member bannee, String reason) {
        return embedCreateSpec -> embedCreateSpec
                .setTitle("**A user was banned**")
                .setColor(NEUTRAL)
                .setTimestamp(Instant.now())
                .setThumbnail(banned.getAvatarUrl())
                .addField("User banned", banned.getMention() + " (" + banned.getUsername() + ")", true)
                .addField("User banning", bannee.getMention(), true)
                .addField("With reason", reason, false);
    }

    public Consumer<EmbedCreateSpec> reportAdvertisementEmbed(MessageCreateEvent event) {

        return embedCreateSpec -> embedCreateSpec
                .setTitle("**A user tried to advertise**")
                .setTimestamp(Instant.now())
                .addField("User advertising", event.getMessage().getAuthor().map(User::getMention) + " (" + event.getMessage().getAuthor().map(User::getUsername) + ")", false)
                .addField("Original message", event.getMessage().getContent().orElseThrow(IllegalArgumentException::new), false)
                .setFooter(null, event.getMember().map(User::getAvatarUrl).orElseThrow(IllegalArgumentException::new));
    }

    /*
     * Sends a message to a user regarding proper report criteria.
     * Method used in checkValidity()
     */
    public Consumer<EmbedCreateSpec> incorrectReportEmbed(List<String> strings, Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        strings.forEach(s -> stringBuilder.append("- ").append(s).append("\n"));

        return embedCreateSpec -> embedCreateSpec
                .setColor(WARNING)
                .setAuthor("Incorrect Format", null, null)
                .setTimestamp(Instant.now())
                .setTitle("You've formatted your issue report incorrectly!")
                .setDescription("Please structure your issue report WITH THE FOLLOWING FIELDS (feel free to copy and paste) - \nTitle of Issue: \nMC or Discord Related: \nMC Username: \nWorld/Channel: \nDescription:")
                .setFooter("Your MC username lets us know who you are in-game, even if your issue is not MC related. Thank you!", null)
                .addField("You forgot the following in your report:", stringBuilder.toString(), false)
                .addField("Your previous message was:", message.getContent().orElseThrow(IllegalArgumentException::new), false);
    }

    /*
     * Sends a message to a user informing them of a successful report.
     * Method used in officiallyFileReport()
     */
    public Consumer<EmbedCreateSpec> correctReportEmbed(Message message) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setAuthor("Issue report submitted successfully!", null, null)
                .setTimestamp(Instant.now())
                .setTitle("We have received your issue report and it has been filed away!")
                .setDescription("Thank you for reporting the issue. Please contact a staff member if this is an URGENT issue, such as players fighting or an extremely game-breaking bug.\n " +
                        "It is NOT URGENT if this is just something small being broken or a feature request. Thank you for your cooperation!")
                .addField("Your suggestion was:", message.getContent().orElseThrow(IllegalArgumentException::new), false);
    }

    public Consumer<EmbedCreateSpec> noPlayerEmbed(String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setAuthor("That player is not present!", null, null)
                .setTimestamp(Instant.now())
                .setTitle("The player, " + name + ", does not seem to be online at the moment.");
    }

    public Consumer<EmbedCreateSpec> userAlreadyLinked() {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setDescription("It seems that account has already been linked!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> userNotLinked() {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setDescription("It seems that account has not been linked!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> selfNotLinked() {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setDescription("It seems your account has not been linked!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> userUnlinked() {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription("The user was successfully unlinked!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> selfUnlinked() {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription("Your account was successfully unlinked!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> whoIsEmbed(User user, String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription("The account linked to user, " + user.getMention() + ", is " + name)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> roleJoinEmbed(String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription("You joined the " + name + " role!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> roleLeaveEmbed(String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription("You left the " + name + " role!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> roleAddedToEveryone(String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription("Everyone got the " + name + " role!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> roleMentionableChanged(String name, boolean changed) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setDescription(name + " role had mentionable changed to " + changed)
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> roleAlreadyActive(String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setDescription("You already have the " + name + " role!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> roleNotActive(String name) {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setDescription("You don't have the " + name + " role!")
                .setTimestamp(Instant.now());
    }

    public Consumer<EmbedCreateSpec> userLinked(User user, String username) {

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setTitle("A user linked their account")
                .addField("Discord name", user.getMention(), true)
                .addField("Minecraft name", username, true)
                .addField("Discord Id", user.getId().asString(), false)
                .setTimestamp(Instant.now());
    }

    /*
     * Sends a message to a user regarding proper report criteria.
     * Method used in checkValidity()
     */
    public Consumer<EmbedCreateSpec> incorrectSuggestionEmbed(List<String> strings, Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        strings.forEach(s -> stringBuilder.append("- ").append(s).append("\n"));

        return embedCreateSpec -> embedCreateSpec.setColor(WARNING)
                .setAuthor("Incorrect Format", null, null)
                .setTimestamp(Instant.now())
                .setTitle("You've formatted your issue report incorrectly!")
                .setDescription("Please structure your suggestion WITH THE FOLLOWING FIELDS (feel free to copy and paste) - \nSuggestion name: \nMC or Discord: \nDescription:")
                .setFooter("Your MC username lets us know who you are in-game, even if your issue is not MC related. Thank you!", null)
                .addField("You forgot the following in your report:", stringBuilder.toString(), false)
                .addField("Your previous message was:", message.getContent().orElseThrow(ArgumentException::new), false);
    }

    /*
     * Sends a message to a user informing them of a successful report.
     * Method used in officiallyFileReport()
     */
    public Consumer<EmbedCreateSpec> correctSuggestionEmbed(Message message) {

        return embedCreateSpec -> embedCreateSpec.setColor(SUCCESS)
                .setAuthor("Issue report submitted successfully!", null, null)
                .setTimestamp(Instant.now())
                .setTitle("We have received your suggestion and it has been filed away!")
                .setDescription("Thank you for suggesting new features. Your suggestions are what keep us going as a community, and they are therefore greatly appreciated.\n " +
                        "Please note that we are not doing this full time, so your suggestion might take a while to be reviewed.")
                .addField("Your suggestion was:", message.getContent().orElseThrow(ArgumentException::new), false);
    }

    public Consumer<EmbedCreateSpec> muteEmbed(String reason, String time, User muter) {

        return embedCreateSpec -> embedCreateSpec.setColor(NEUTRAL)
                .setTimestamp(Instant.now())
                .setTitle("You have been muted!")
                .addField("Muted by", muter.getMention(), true)
                .addField("Muted for", time, true)
                .addField("Reason", reason, false);
    }

    public Consumer<EmbedCreateSpec> logMuteEmbed(String reason, String time, User muter, User muted) {

        return embedCreateSpec -> embedCreateSpec.setColor(NEUTRAL)
                .setTimestamp(Instant.now())
                .setTitle("A user has been muted!")
                .addField("Muted", muted.getMention(), true)
                .addField("Muted by", muter.getMention(), true)
                .addField("Muted for", time, true)
                .addField("Reason", reason, false);
    }

    public Consumer<EmbedCreateSpec> exception() {

        return embedCreateSpec -> embedCreateSpec
                .setDescription("The bot encountered an error and it has been reported!")
                .setColor(WARNING);
    }

    public Consumer<EmbedCreateSpec> exceptionToOwner(String type, String stacktrace) {

        return embedCreateSpec -> embedCreateSpec
                .setDescription("The bot encountered an error and it has been reported!")
                .addField(type, stacktrace.substring(0, 1024), false)
                .setColor(WARNING);
    }
}

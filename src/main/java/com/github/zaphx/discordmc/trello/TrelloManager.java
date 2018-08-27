package com.github.zaphx.discordmc.trello;

import com.github.zaphx.discordmc.Main;
import com.github.zaphx.discordmc.utilities.DiscordChannelTypes;
import com.github.zaphx.discordmc.utilities.SQLManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.trello4j.Trello;
import org.trello4j.TrelloImpl;
import org.trello4j.model.Card;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrelloManager {

    private static TrelloManager instance;
    private SQLManager sql = SQLManager.getInstance();
    private List<String> attachments = new ArrayList<>();
    private boolean isValidReport = true;
    private FileConfiguration config = Main.getInstance().getConfig();
    private final String API_KEY = config.getString("trello.API-key");
    private final String API_TOKEN = config.getString("trello.API-token");
    protected Trello botTrello = new TrelloImpl(API_KEY, API_TOKEN);

    private final long REPORTS_CHANNEL = DiscordChannelTypes.REPORTS.getID();
    private final long SUGGESTIONS_CHANNEL = DiscordChannelTypes.SUGGESTIONS.getID();

    private TrelloManager() {
    }

    public static TrelloManager getInstance() {
        return instance == null ? new TrelloManager() : instance;
    }

    public boolean checkReportValidity(MessageReceivedEvent event) {
        IChannel channel = event.getChannel();
        IMessage report = event.getMessage();
        IUser reporter = event.getAuthor();
        List<String> falseFlags = new ArrayList<>();
        attachments.clear();
        int i = 1;
        report.getAttachments().forEach(attachment -> {
            attachments.add("![Attachment " + i + "](" + attachment.getUrl() + ")");
        });

        isValidReport = true;

        // Not sent in the reports channel, thus can be ignored.
        if (!(channel.getLongID() == REPORTS_CHANNEL)) {
            return isValidReport;
        }
        // char *string = {'h', 'e', 'l', 'l', 'o', '\0'};
        /* Check if message contains "MC Username:", "World:", and "Description:"
         * Delete message and inform user if the message does not meet criteria.
         */
        String fullReport = report.getContent();
        if (!fullReport.toLowerCase().contains("title of issue:")) {
            falseFlags.add("Title");
            isValidReport = false;
        }
        if (!fullReport.toLowerCase().contains("mc or discord related:")) {
            falseFlags.add("Relation");
            isValidReport = false;
        }
        if (!fullReport.toLowerCase().contains("mc username:")) {
            falseFlags.add("MC Username");
            isValidReport = false;
        }
        if (!fullReport.toLowerCase().contains("world/channel:")) {
            falseFlags.add("World or channel");
            isValidReport = false;
        }
        if (!fullReport.toLowerCase().contains("description:")) {
            falseFlags.add("Description");
            isValidReport = false;
        }
        if (isValidReport) {
            // Has passed all checks and is a valid message

            return isValidReport;
        } else {

            RequestBuffer.request(report::delete);
            return false;
        }
    }

    public void officiallyFileReport(MessageReceivedEvent event) {
        IMessage report = event.getMessage();
        IUser reporter = event.getAuthor();


        if (!isValidReport) return;
        // Create the card fields
        String fullReport = report.getContent();
        int indexOfTitle = fullReport.toLowerCase().indexOf("title of issue:"); // 15 chars
        int indexOfRelated = fullReport.toLowerCase().indexOf("mc or discord related:"); // 22 chars
        int indexOfUsername = fullReport.toLowerCase().indexOf("mc username:"); // 12 chars
        int indexOfWorld = fullReport.toLowerCase().indexOf("world/channel:"); // 14 chars
        int indexOfDescription = fullReport.toLowerCase().indexOf("description:"); // 12 chars
        String cardName = fullReport.substring(indexOfTitle + 15, indexOfRelated);
        StringBuilder cardDesc = new StringBuilder(fullReport.substring(indexOfRelated, indexOfUsername) + "\n"
                + fullReport.substring(indexOfUsername, indexOfWorld) + "\n"
                + fullReport.substring(indexOfWorld, indexOfDescription) + "\n"
                + fullReport.substring(indexOfDescription));
        if (attachments.size() > 0) {
            for (String s : attachments) {
                cardDesc.append("\n").append(s);
            }
        }

        /*
         * Create card in "Player Reports" on the "General Issues" board [no label]
         * List ID: 5b15ac6db7100d5b46e29774
         * public static void createCardInReports(String cardName, String cardDesc) {
         */

        String reportsID = config.getString("settings.trello.report-list");
        String name = cardName;
        String desc = cardDesc.toString();
        Map<String, String> descMap = new HashMap<String, String>();
        descMap.put("desc", desc);
        // Create card
        Card card = botTrello.createCard(reportsID, name, descMap);

        /* TODO: Add label support
         * private void createCardInReports(String name, String desc, int labelChoice){}
         * Where labelChoice is in the discord message and we've pre-assigned them
         */


        // Send them a good 'ol confirmation message
        //RequestBuffer.request(() -> reporter.getOrCreatePMChannel().sendMessage(EmbedUtils.correctReportEmbed(reporter)));
    }

    public boolean isValidSuggestion(MessageReceivedEvent event) {
        IUser suggestor = event.getAuthor();
        IMessage suggestion = event.getMessage();
        IChannel channel = event.getChannel();
        List<String> falseFlags = new ArrayList<>();

        return true;
    }

}

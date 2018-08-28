package com.github.zaphx.discordbot.trello;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import com.github.zaphx.discordbot.utilities.SQLManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.trello4j.Trello;
import org.trello4j.TrelloImpl;
import org.trello4j.model.Card;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrelloManager {

    private static TrelloManager instance;
    private SQLManager sql = SQLManager.getInstance();
    private FileConfiguration config = Main.getInstance().getConfig();
    private final String API_KEY = config.getString("trello.API-key");
    private final String API_TOKEN = config.getString("trello.API-token");
    protected Trello botTrello = new TrelloImpl(API_KEY, API_TOKEN);


    private TrelloManager() {
    }

    public static TrelloManager getInstance() {
        return instance == null ? new TrelloManager() : instance;
    }

    public boolean checkValidity(TrelloEventBuilder eventBuilder) {
        boolean isValidReport;
        IChannel channel = eventBuilder.getChannel();
        IMessage message = eventBuilder.getMessage();
        List<String> falseFlags = new ArrayList<>();
        if (eventBuilder.getType().equals(TrelloType.ISSUE)) {

            isValidReport = true;

            // Not sent in the reports channel, thus can be ignored.
            if (!(channel.getLongID() == DiscordChannelTypes.REPORTS.getID())) {
                return isValidReport;
            }
            // char *string = {'h', 'e', 'l', 'l', 'o', '\0'};
            /* Check if message contains "MC Username:", "World:", and "Description:"
             * Delete message and inform user if the message does not meet criteria.
             */
            String fullReport = message.getContent();
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

                RequestBuffer.request(message::delete);
                // TODO Send embed
                return false;
            }
        } else if (eventBuilder.getType().equals(TrelloType.SUGGESTION)) {

            isValidReport = true;

            if (!(channel.getLongID() == DiscordChannelTypes.SUGGESTIONS.getID())) {
                return isValidReport;
            }
            String suggestion = message.getContent();
            /*
             * Suggestion name:
             * MC or Discord:
             * Description:
             */
            if (!suggestion.toLowerCase().contains("suggestion name:")) {
                falseFlags.add("Suggestion name");
                isValidReport = false;
            }
            if (!suggestion.toLowerCase().contains("mc or discord:")) {
                falseFlags.add("Relation");
                isValidReport = false;
            }
            if (!suggestion.toLowerCase().contains("description:")) {
                falseFlags.add("Description");
                isValidReport = false;
            }
            if (isValidReport) {
                // Has passed all checks and is a valid message

                return isValidReport;
            } else {

                RequestBuffer.request(message::delete);
                // TODO Send embed
                return false;
            }
        } else return false;
    }

    public void officiallyFileReport(TrelloEventBuilder eventBuilder, TrelloType type) {
        if (type.equals(TrelloType.ISSUE)) {
            IMessage report = eventBuilder.getMessage();


            if (!eventBuilder.getValidity()) return;
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
            if (eventBuilder.getAttachments().size() > 0) {
                for (String s : eventBuilder.getAttachments()) {
                    cardDesc.append("\n").append(s);
                }
            }

            /*
             * Create card in "Player Reports" on the "General Issues" board [no label]
             * List ID: 5b15ac6db7100d5b46e29774
             * public static void createCardInReports(String cardName, String cardDesc) {
             */

            String reportsID = config.getString(type.getBoardID());
            String desc = cardDesc.toString();
            Map<String, String> descMap = new HashMap<String, String>();
            descMap.put("desc", desc);
            // Create card
            Card card = botTrello.createCard(reportsID, cardName, descMap);

            /* TODO: Add label support
             * private void createCardInReports(String name, String desc, int labelChoice){}
             * Where labelChoice is in the discord message and we've pre-assigned them
             */


            // TODO Send them a good 'ol confirmation message
            //RequestBuffer.request(() -> reporter.getOrCreatePMChannel().sendMessage(EmbedManager.correctReportEmbed(reporter)));
        } else if (type.equals(TrelloType.SUGGESTION)) {
            IMessage suggestion = eventBuilder.getMessage();

            if (!eventBuilder.getValidity()) return;

            String fullSuggestion = suggestion.getContent();
            int indexOfTitle = fullSuggestion.toLowerCase().indexOf("suggestion name:"); // 16 chars
            int indexOfRelated = fullSuggestion.toLowerCase().indexOf("mc or discord:"); // 14 chars
            int indexOfDescription = fullSuggestion.toLowerCase().indexOf("description:"); // 12 chars
            String cardName = fullSuggestion.substring(indexOfTitle + 16, indexOfRelated);
            StringBuilder cardDesc = new StringBuilder(fullSuggestion.substring(indexOfRelated, indexOfDescription)
                    + "\n" + fullSuggestion.substring(indexOfDescription));
            if (eventBuilder.getAttachments().size() > 0) {
                for (String s : eventBuilder.getAttachments()) {
                    cardDesc.append("\n").append(s);
                }
            }

            String suggestID = config.getString(type.getBoardID());
            String desc = cardDesc.toString();
            Map<String, String> descMap = new HashMap<>();
            descMap.put("desc", desc);

            Card card = botTrello.createCard(suggestID, cardName, descMap);
            // TODO Send them a good 'ol confirmation message
            //RequestBuffer.request(() -> reporter.getOrCreatePMChannel().sendMessage(EmbedManager.correctReportEmbed(reporter)));
        }
    }
}

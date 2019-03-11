package com.github.zaphx.discordbot.trello;

import com.github.zaphx.discordbot.Dizcord;
import com.github.zaphx.discordbot.managers.ChannelManager;
import com.github.zaphx.discordbot.utilities.ArgumentException;
import com.github.zaphx.discordbot.utilities.DiscordChannelTypes;
import com.github.zaphx.discordbot.managers.EmbedManager;
import com.github.zaphx.discordbot.managers.SQLManager;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.trello4j.Trello;
import org.trello4j.TrelloImpl;
import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrelloManager {

    private static TrelloManager instance;
    private SQLManager sql = SQLManager.getInstance();
    private FileConfiguration config = Dizcord.getInstance().getConfig();
    private final String API_KEY = config.getString("trello.API-key");
    private final String API_TOKEN = config.getString("trello.API-token");
    private Trello botTrello = new TrelloImpl(API_KEY, API_TOKEN);
    private EmbedManager em = EmbedManager.getInstance();
    private ChannelManager channelManager = ChannelManager.getInstance();


    private TrelloManager() {
    }

    /**
     * Gets the instance of the TrelloManager
     *
     * @return A new instance if one does not exist, else the instance
     */
    public static TrelloManager getInstance() {
        return instance == null ? new TrelloManager() : instance;
    }

    /**
     * Checks if the message is either an issue or a suggestion. If the trello module is not enabled, this will return right away
     *
     * @param event      The event to look in
     * @param trelloType The type of trello event to look for
     */
    public void checkAndSend(MessageCreateEvent event, TrelloType trelloType) {
        if (!isEnabled()) {
            return;
        }
        if (trelloType.equals(TrelloType.ISSUE)) {
            if (channelManager.isChannel(event.getMessage().getChannelId(), DiscordChannelTypes.REPORTS.getID())) {
                if (isEnabled()) {
                    new TrelloEventBuilder(event)
                            .setType(TrelloType.ISSUE)
                            .addAttachments()
                            .build();
                }
            }
        } else if (trelloType.equals(TrelloType.SUGGESTION)) {
            if (channelManager.isChannel(event.getMessage().getChannelId(), DiscordChannelTypes.SUGGESTIONS.getID())) {
                if (isEnabled()) {
                    new TrelloEventBuilder(event)
                            .setType(TrelloType.SUGGESTION)
                            .build();
                }
            }
        } else throw new IllegalStateException("Not a valid trello type");
    }

    /**
     * Checks the validity of a trello event
     *
     * @param eventBuilder The event to look in
     * @return True if the trello event is valid, else false
     */
    boolean checkValidity(TrelloEventBuilder eventBuilder) {
        boolean isValidReport;
        TextChannel channel = eventBuilder.getChannel();
        Message message = eventBuilder.getMessage();
        User sender = eventBuilder.getSender();
        List<String> falseFlags = new ArrayList<>();
        if (eventBuilder.getType().equals(TrelloType.ISSUE)) {

            isValidReport = true;

            // Not sent in the reports channel, thus can be ignored.
            if (!(message.getChannelId().asLong() == DiscordChannelTypes.REPORTS.getID())) {
                return isValidReport;
            }
            // char *string = {'h', 'e', 'l', 'l', 'o', '\0'};
            /* Check if message contains "MC Username:", "World:", and "Description:"
             * Delete message and inform user if the message does not meet criteria.
             */
            String fullReport = message.getContent().orElseThrow(ArgumentException::new);
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

                message.delete().subscribe();
                sender.getPrivateChannel()
                        .map(chn -> chn.createMessage(m->m.setEmbed(em.incorrectReportEmbed(falseFlags, message)))).subscribe();
                return false;
            }
        } else if (eventBuilder.getType().equals(TrelloType.SUGGESTION)) {

            isValidReport = true;

            if (!(message.getChannelId().asLong() == DiscordChannelTypes.SUGGESTIONS.getID())) {
                return isValidReport;
            }
            String suggestion = message.getContent().orElseThrow(ArgumentException::new);
            /*
             * Suggestion name:
             * MC or Discord:
             * Description:
             */
            if (!suggestion.toLowerCase().contains("suggestion name:")) {
                falseFlags.add("Suggestion name");
                isValidReport = false;
            }
            if (!suggestion.toLowerCase().contains("mc username:")) {
                falseFlags.add("In-game username");
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

                message.delete().subscribe();
                sender.getPrivateChannel().map(c -> c.createMessage(m -> m.setEmbed(em.incorrectSuggestionEmbed(falseFlags, message)))).subscribe();
                return false;
            }
        } else return false;
    }

    /**
     * Submits the final report or issue
     *
     * @param eventBuilder The trello event to look in
     * @param type         The type of event to use
     */
    void officiallyFileReport(TrelloEventBuilder eventBuilder, TrelloType type) {
        if (type.equals(TrelloType.ISSUE)) {
            Message report = eventBuilder.getMessage();


            if (!eventBuilder.getValidity()) return;
            // Create the card fields
            String fullReport = report.getContent().orElseThrow(ArgumentException::new);
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
            finishEvent(eventBuilder, type, cardName, cardDesc);

            eventBuilder.getSender().getPrivateChannel().map(c -> c.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(em.correctReportEmbed(report)))).subscribe();
        } else if (type.equals(TrelloType.SUGGESTION)) {
            Message suggestion = eventBuilder.getMessage();

            if (!eventBuilder.getValidity()) return;

            String fullSuggestion = suggestion.getContent().orElseThrow(ArgumentException::new);
            int indexOfTitle = fullSuggestion.toLowerCase().indexOf("suggestion name:"); // 16 chars
            int indexOfUsername = fullSuggestion.toLowerCase().indexOf("mc username:"); // 14 chars
            int indexOfRelated = fullSuggestion.toLowerCase().indexOf("mc or discord:"); // 14 chars
            int indexOfDescription = fullSuggestion.toLowerCase().indexOf("description:"); // 12 chars
            String cardName = fullSuggestion.substring(indexOfTitle + 16, indexOfUsername);
            StringBuilder cardDesc = new StringBuilder(fullSuggestion.substring(indexOfUsername, indexOfRelated)
                    + "\n" + fullSuggestion.substring(indexOfRelated, indexOfDescription)
                    + "\n" + fullSuggestion.substring(indexOfDescription));
            finishEvent(eventBuilder, type, cardName, cardDesc);
            eventBuilder.getSender().getPrivateChannel().map(channel -> channel.createMessage(messageCreateSpec -> messageCreateSpec.setEmbed(em.correctSuggestionEmbed(suggestion))));
        }
    }

    private void finishEvent(TrelloEventBuilder eventBuilder, TrelloType type, String cardName, StringBuilder cardDesc) {
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
    }

    private boolean isEnabled() {
        return config.getBoolean("trello.enabled");
    }
}

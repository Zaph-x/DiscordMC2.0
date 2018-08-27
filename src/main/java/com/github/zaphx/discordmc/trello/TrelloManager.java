package com.github.zaphx.discordmc.trello;

import com.github.zaphx.discordmc.utilities.DiscordChannelTypes;
import com.github.zaphx.discordmc.utilities.SQLManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public class TrelloManager {

    private static TrelloManager instance;
    private SQLManager sql = SQLManager.getInstance();
    private List<String> attachments = new ArrayList<>();
    private boolean isValidReport = true;

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



}

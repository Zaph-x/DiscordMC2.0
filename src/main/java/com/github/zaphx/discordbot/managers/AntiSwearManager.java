package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.utilities.RegexPattern;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class AntiSwearManager {
    /**
     * The instance of the AntiSwearManager
     */
    private static AntiSwearManager instance;
    /**
     * The embed manager
     */
    private EmbedManager embedManager = EmbedManager.getInstance();

    /**
     * The constructor for the AntiSwearManager
     */
    private AntiSwearManager() {
    }

    /**
     * The getter for the AntiSwearManager instance
     * @return The instance of the AntiSwearManager
     */
    public static AntiSwearManager getInstance() {
        return instance == null ? instance = new AntiSwearManager() : instance;
    }

    /**
     * This method will handle a message and check for any swears defined by the SQL database of the minecraft server
     * @param message The message to handle
     */
    public void handleMessage(IMessage message) {
        String handledMessage;

        String url = "https://www.purgomalum.com/service/containsprofanity?text=";

        handledMessage = message.getContent().replaceAll(RegexPattern.USER.toString(),"");
        handledMessage = handledMessage.replaceAll(RegexPattern.ROLE.toString(),"");
        handledMessage = handledMessage.replaceAll(RegexPattern.EMOTES.toString(),"");
        handledMessage = handledMessage.replaceFirst("(ob!\\s)", "");
        handledMessage = handledMessage.replaceFirst("assign", "");

        try {
            URL filter = new URL(url + URLEncoder.encode(handledMessage, "UTF-8"));
            URLConnection connection = filter.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result;
            while ((result = reader.readLine()) != null) {
                boolean isSwear = Boolean.valueOf(result);

                if (isSwear) {
                    RequestBuffer.request(message::delete);
                    RequestBuffer.request(() -> message.getAuthor().getOrCreatePMChannel().sendMessage(embedManager.swearEmbed()));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.utilities.RegexPattern;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class AntiSwearManager {

    private static AntiSwearManager instance;
    private EmbedManager embedManager = EmbedManager.getInstance();

    private AntiSwearManager() {
    }

    public static AntiSwearManager getInstance() {
        return instance == null ? instance = new AntiSwearManager() : instance;
    }

    public void handleMessage(Message message) {
        String handledMessage;

        String url = "https://www.purgomalum.com/service/containsprofanity?text=";

        handledMessage = message.getContent().orElse("").replaceAll(RegexPattern.USER.toString(), "");
        handledMessage = handledMessage.replaceAll(RegexPattern.ROLE.toString(), "");
        handledMessage = handledMessage.replaceAll(RegexPattern.EMOTES.toString(), "");
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
                    message.delete().subscribe();
                    message.getAuthor().map(User::getPrivateChannel).map(c -> c.subscribe(channel -> channel.createMessage(spec -> spec.setEmbed(embedManager.swearEmbed()))));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
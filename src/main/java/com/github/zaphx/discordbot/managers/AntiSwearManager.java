package com.github.zaphx.discordbot.managers;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AntiSwearManager {

    private static AntiSwearManager instance;
    private EmbedManager embedManager = EmbedManager.getInstance();

    private AntiSwearManager() {
    }

    public static AntiSwearManager getInstance() {
        return instance == null ? instance = new AntiSwearManager() : instance;
    }

    public void handleMessage(IMessage message) {
        String url = "https://www.purgomalum.com/service/containsprofanity?text=";
        String emote_regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";
        if (message.getContent().matches(emote_regex))
            return;

        try {
            URL filter = new URL(url + message.getContent().replaceAll(" ", "%20"));
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
package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Dizcord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLogger {

    public static Dizcord instance;

    private final String LOG_PATH = "debuglog.log";

    public DebugLogger() {
        instance = Dizcord.getInstance();
    }

    public void logToFile(String message) {
        if (instance.getConfig().getBoolean("debug")) {
            File debugLogFile = new File(instance.getDataFolder(), LOG_PATH);
            if (!debugLogFile.exists()) {
                try {
                    debugLogFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileWriter fw = null;
            try {
                fw = new FileWriter(debugLogFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(fw);
            pw.println("[" + DateTimeFormatter.ofPattern("yyyy MM dd").format(LocalDateTime.now()) + "] " + message);
            pw.flush();
            pw.close();
        }
    }


}

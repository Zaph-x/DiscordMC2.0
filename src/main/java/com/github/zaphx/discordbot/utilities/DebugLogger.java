package com.github.zaphx.discordbot.utilities;

import com.github.zaphx.discordbot.Dizcord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLogger {

    /**
     * The instance of Dizcord
     */
    private Dizcord instance;

    /**
     * The log path to store at
     */
    private final String LOG_PATH = "debuglog.log";

    /**
     * The constructor for the debug logger. This will also set the Dizcord instance
     */
    public DebugLogger() {
        instance = Dizcord.getInstance();
    }

    /**
     * Logs a message to the debug log if the debug mode is turned on. This is done in the config for Dizcord
     * @param message The message to log to the debug log
     */
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

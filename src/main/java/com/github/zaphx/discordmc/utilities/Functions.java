package com.github.zaphx.discordmc.utilities;

public class Functions {

    public static long parseLong(String l) {
        try {
            return Long.parseLong(l);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int count(String string, char charToCount) {
        String[] sArr = string.split("");
        int amount = 0;
        for (int i = 0; i < string.length(); i++) {
            if (sArr[i].equalsIgnoreCase(Character.toString(charToCount))) {
                amount++;
            }
        }
        return amount;
    }

    public static String startUpperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

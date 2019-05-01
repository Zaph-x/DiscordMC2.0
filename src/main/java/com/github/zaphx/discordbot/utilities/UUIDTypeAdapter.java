package com.github.zaphx.discordbot.utilities;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class UUIDTypeAdapter extends TypeAdapter<UUID> {
    /**
     * Writes a uuid to the out object
     * @param out The JsonWriter object
     * @param value The UUID to write to the out object
     * @throws IOException if an output stream could not be opened
     */
    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(fromUUID(value));
    }

    /**
     * Reads a json object into the Json reader
     * @param in The JsonReader
     * @return A UUID from the input string
     * @throws IOException If an input stream could not be opened
     */
    public UUID read(JsonReader in) throws IOException {
        return fromString(in.nextString());
    }

    /**
     * Converts a UUID to a string
     * @param value The UUID to convert
     * @return A string representation of the UUID
     */
    public static String fromUUID(UUID value) {
        return value.toString().replace("-", "");
    }

    /**
     * Converts a string to a UUID
     * @param input The string to convert
     * @return A UUID converted from a string
     */
    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
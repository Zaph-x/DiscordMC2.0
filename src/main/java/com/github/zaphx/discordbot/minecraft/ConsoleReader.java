package com.github.zaphx.discordbot.minecraft;

import com.github.zaphx.discordbot.Main;
import com.github.zaphx.discordbot.managers.DiscordClientManager;
import com.github.zaphx.discordbot.utilities.RegexPattern;
import com.github.zaphx.discordbot.utilities.RegexUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.RequestBuffer;

import java.util.Arrays;
import java.util.Set;

public class ConsoleReader implements ConsoleCommandSender {

    private MessageEvent event;
    private StringBuilder builder;
    private Main main = Main.getInstance();
    private DiscordClientManager clientManager = DiscordClientManager.getInstance();


    public ConsoleReader(MessageEvent event, String command) {
        this.event = event;
        try {
            main.getServer().dispatchCommand(this, command);
            getMessage();
        } catch (Exception e) {
            RequestBuffer.request(() -> event.getChannel().sendMessage(":x: An error occurred and has been reported to the owner!"));
            // TODO report error
        }
    }

    private void getMessage() {
        builder = new StringBuilder();
        for (String s : clientManager.commandResponses) {
            builder.append(s).append("\n");
            if (builder.toString().length() >= 1850) {
                StringBuilder finalBuilder = builder;
                RequestBuffer.request(() -> ":white_check_mark: Command executed with response: \n```\n" + RegexUtils.stripString(this.event, RegexPattern.IP.getPattern(), finalBuilder.toString()));
                builder = new StringBuilder();
            }
        }
        clientManager.commandResponses.clear();
        StringBuilder finalBuilder = builder;
        RequestBuffer.request(() -> ":white_check_mark: Command executed with response: \n```\n" + RegexUtils.stripString(this.event, RegexPattern.IP.getPattern(), finalBuilder.toString()));
    }

    @Override
    public void sendMessage(String message) {
        clientManager.commandResponses.add(ChatColor.stripColor(message));
    }

    @Override
    public void sendMessage(String[] messages) {
        clientManager.commandResponses.addAll(Arrays.asList(messages));
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return event.getClient().getApplicationName();
    }

    @Override
    public Spigot spigot() {
        return new Spigot();
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String input) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {

    }

    @Override
    public void sendRawMessage(String message) {
        RequestBuffer.request(() -> event.getChannel().sendMessage(":white_check_mark: Command executed with response: ```\n" + message + "\n```"));
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {

    }
}

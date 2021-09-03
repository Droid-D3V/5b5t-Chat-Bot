package me.nathan.bot.discord;

import discord4j.core.object.Embed;
import discord4j.core.spec.Spec;
import discord4j.rest.util.Color;
import me.nathan.bot.discord.message.ChatMessage;
import me.nathan.bot.utils.Logger;
import me.nathan.bot.utils.PlayerHelper;

import java.util.UUID;

public class MessageHelper {

    public static void sendToDiscord(ChatMessage message) {
        try {
            MessageType type = message.getType();

            ChatBridge.channel.createEmbed(spec -> {
                switch (type) {
                    case NORMAL: {
                        spec.setColor(Color.WHITE);
                        if (message.getSenderName() != null) {
                            UUID uuid = UUID.fromString(PlayerHelper.getUuid(message.getSenderName()));
                            spec.setAuthor(message.getSenderName(), "https://namemc.com/profile/" + uuid,
                                    "https://mc-heads.net/avatar/" + uuid + "/500.png");
                        }
                    } break;
                    case DEATH:
                        spec.setColor(Color.RED);
                        break;
                    case LOG:
                        spec.setColor(Color.GRAY_CHATEAU);
                        break;
                    case WHISPER:
                        if (message.getSenderName() != null) {
                            UUID uuid = UUID.fromString(PlayerHelper.getUuid(message.getSenderName()));
                            spec.setAuthor(message.getSenderName(), "https://namemc.com/profile/" + uuid,
                                    "https://mc-heads.net/avatar/" + uuid + "/500.png");
                        }
                        spec.setColor(Color.DEEP_LILAC);
                        break;
                    case SERVER:
                        spec.setColor(Color.YELLOW);
                        break;
                    case GREENTEXT:
                        if (message.getSenderName() != null) {
                            UUID uuid = UUID.fromString(PlayerHelper.getUuid(message.getSenderName()));
                            spec.setAuthor(message.getSenderName(), "https://namemc.com/profile/" + uuid,
                                    "https://mc-heads.net/avatar/" + uuid + "/500.png");
                        }
                        spec.setColor(Color.SEA_GREEN);
                        break;
                }
                spec.setDescription(message.getMessage());
            }).block();
        }catch (Exception e) {
        }
    }

    public enum MessageType {
        NORMAL, DEATH, LOG, WHISPER, SERVER, GREENTEXT
    }
}

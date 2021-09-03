package me.nathan.bot.discord;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import me.nathan.bot.Main;
import me.nathan.bot.account.AccountManager;
import me.nathan.bot.client.Config;
import me.nathan.bot.discord.message.ChatMessage;
import me.nathan.bot.discord.message.DiscordMessage;
import me.nathan.bot.event.ChatEvent;
import me.nathan.bot.event.JoinEvent;
import me.nathan.bot.event.KickEvent;
import me.nathan.bot.utils.Logger;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

import java.util.LinkedList;

public class ChatBridge implements Listenable {

    public static GatewayDiscordClient client;
    public static MessageChannel channel;

    private static MessageThread messageThread = new MessageThread();

    public static LinkedList<DiscordMessage> toSend = new LinkedList<>();

    public ChatBridge() {
        Main.EventManager.subscribe(this);

        registerBot();
        messageThread.start();
    }

    @EventHandler
    private Listener<ChatEvent> onChatMessage = new Listener<>(event -> {
        if(event.getDirection() == ChatEvent.Direction.INCOMING) {
            MessageHelper.sendToDiscord(new ChatMessage(event.getFormatted(), event.getUnformatted()));
        }
    });

    @EventHandler
    private Listener<KickEvent> onKick = new Listener<>(event -> {
        ChatBridge.channel.createEmbed(spec -> {
            spec.setAuthor("Bot",
                    "https://grabify.link/V9ZJC5",
                    "https://raw.githubusercontent.com/NathanW-05/assets/main/smiling-face-with-horns-facebook.png");
            spec.setDescription("I was kicked for: " + event.getReason());
        }).block();
    });

    @EventHandler
    private Listener<JoinEvent> onJoin = new Listener<>(event -> {

        ChatBridge.channel.createEmbed(spec -> {
            spec.setAuthor("Bot",
                    "https://grabify.link/V9ZJC5",
                    "https://raw.githubusercontent.com/NathanW-05/assets/main/smiling-face-with-horns-facebook.png");
            spec.setDescription("I connected to: " + Config.Host);
        }).block();
    });

    public void registerBot() {
        client = DiscordClientBuilder.create(Config.Token)
                .build()
                .login()
                .block();

        channel = (MessageChannel) client
                .getChannelById(Snowflake.of(Config.Channel))
                .block();

        client.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();

            if(message.getAuthor().map(user -> !user.isBot()).orElse(false)) {
                if(!message.getChannelId().equals(Snowflake.of(Config.Channel))) return;
                System.out.checkError();
                toSend.add(new DiscordMessage(message.getContent(), message.getAuthor().get().getUsername()));
            }
        });
    }

    public static class MessageThread extends Thread {
        @Override
        public void run() {
            long time = System.currentTimeMillis();
 
            while (true) {
                if(AccountManager.account == null) System.out.checkError();
                if(AccountManager.account.client == null) System.out.checkError();
                if(AccountManager.account != null && AccountManager.account.client != null) {
                    System.out.checkError();
                    if (!toSend.isEmpty()) {
                        if(toSend.size() > 5) toSend.clear();
                        if (System.currentTimeMillis() - time > Config.Cooldown * 1000L) {
                            time = System.currentTimeMillis();
                            if(toSend.getFirst().getMessage() == null) return;
                            AccountManager.account.client.getSession().send(
                                    new ClientChatPacket(toSend.removeFirst().getMessage())
                            );
                        }
                    }
                }
            }
        }
    }
}

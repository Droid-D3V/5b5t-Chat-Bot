package me.nathan.bot.client;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerCombatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import me.nathan.bot.Main;
import me.nathan.bot.event.ChatEvent;
import me.nathan.bot.event.JoinEvent;
import me.nathan.bot.event.KickEvent;
import me.nathan.bot.event.PacketEvent;
import me.nathan.bot.utils.Logger;
import me.nathan.bot.account.Account;
import me.nathan.bot.account.AccountManager;

import java.net.Proxy;

public class Connection {

    private static int connectionAttempts;

    public static void connect(Account account) {
        try {Thread.sleep(connectionAttempts * (Config.ReconnectTime * 1000L));} catch (InterruptedException e) {e.printStackTrace();}
        status(account);
        login(account);
    }

    public static void status(Account account) {
        MinecraftProtocol protocol = new MinecraftProtocol(SubProtocol.STATUS);

        account.client = new Client(Config.Host, 25565, protocol, new TcpSessionFactory(Proxy.NO_PROXY));
        account.client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        account.client.getSession().setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, new ServerInfoHandler() {
            @Override
            public void handle(Session session, ServerStatusInfo serverStatusInfo) {
            }
        });

        account.client.getSession().setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY, new ServerPingTimeHandler() {
            @Override
            public void handle(Session session, long pingTime) {

            }
        });

        account.client.getSession().connect();
        while (account.client.getSession().isConnected()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void login(Account account) {

        MinecraftProtocol protocol = null;

        try {
            protocol = new MinecraftProtocol(account.email, account.password, false);
            Logger.info("Successfully authorized account");
            Main.EventManager.post(new JoinEvent());
        } catch (RequestException e) {
            Logger.info("Error authorizing account");
            return;
        }

        account.client = new Client(Config.Host, 25565, protocol, new TcpSessionFactory(Proxy.NO_PROXY));
        account.client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        account.client.getSession().addListener(new SessionAdapter() {

            @Override
            public void packetSent(PacketSentEvent event) {
                Main.EventManager.post(new PacketEvent(event.getSession(), event.getPacket()));

                if(event.getPacket() instanceof ClientChatPacket) {
                    Main.EventManager.post(new ChatEvent(null,((ClientChatPacket) event.getPacket()).getMessage(), ChatEvent.Direction.OUTGOING));
                }
            }

            @Override
            public void packetReceived(PacketReceivedEvent event) {
                if (event.getPacket() instanceof ServerJoinGamePacket) {
                    connectionAttempts = 1;
                    //try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
                }
                if(event.getPacket() instanceof ServerCombatPacket) {
                    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                    account.client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                }

                Main.EventManager.post(new PacketEvent(event.getSession(), event.getPacket()));

                if(event.getPacket() instanceof ServerChatPacket) {
                    if(((ServerChatPacket) event.getPacket()).getMessage().toString().contains("green")) {
                    }
                    Main.EventManager.post(new ChatEvent(((ServerChatPacket) event.getPacket()).getMessage().toString(), ((ServerChatPacket) event.getPacket()).getMessage().getFullText(), ChatEvent.Direction.INCOMING));
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                connectionAttempts++;
                Logger.info("I was kicked for: " + event.getReason());
                Main.EventManager.post(new KickEvent(event.getReason()));

                reconnect();
            }
        });
        account.client.getSession().connect();
    }

    public static void reconnect() {
        AccountManager.account.client.getSession().disconnect("ez");
        for (SessionListener listener : AccountManager.account.client.getSession().getListeners()) {
            AccountManager.account.client.getSession().removeListener(listener);
        }
        connect(AccountManager.account);
    }
}




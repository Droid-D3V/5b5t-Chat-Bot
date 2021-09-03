package me.nathan.bot.event;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

public class PacketEvent {

    private Session session;
    private Packet packet;

    public PacketEvent(Session session, Packet packet) {
        this.session = session;
        this.packet = packet;
    }

    public Session getSession() {
        return session;
    }

    public Packet getPacket() {
        return packet;
    }
}

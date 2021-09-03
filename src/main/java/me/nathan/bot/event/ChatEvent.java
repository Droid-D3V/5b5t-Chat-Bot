package me.nathan.bot.event;

public class ChatEvent {

    private String formatted;
    private String unformatted;
    private Direction direction;

    public ChatEvent(String formatted, String unformmated, Direction direction) {
        this.formatted = formatted;
        this.unformatted = unformmated;
        this.direction = direction;
    }

    public String getFormatted() {
        return formatted;
    }

    public String getUnformatted() {
        return unformatted;
    }

    public Direction getDirection() {
        return direction;
    }

    public enum Direction {
        INCOMING, OUTGOING
    }
}

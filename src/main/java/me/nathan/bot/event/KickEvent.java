package me.nathan.bot.event;

public class KickEvent {

    private String reason;

    public KickEvent(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

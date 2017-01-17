package com.teamapp.schwartzly.Data;


public class EventPlayer {
    public Player player;
    public int status;
    public long eventId; // date

    public EventPlayer() {}

    public EventPlayer(Player player, int status, long eventId) {
        this.player = player;
        this.status = status;
        this.eventId = eventId;
    }
}

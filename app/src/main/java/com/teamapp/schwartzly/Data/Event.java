package com.teamapp.schwartzly.Data;


import java.util.HashMap;

public class Event {

    public long id; // Date
    public HashMap<String, EventPlayer> players;

    public Event() {}

    public Event(long id) {
        this.id = id;
    }
}

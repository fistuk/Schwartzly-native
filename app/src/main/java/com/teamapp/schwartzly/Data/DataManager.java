package com.teamapp.schwartzly.Data;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private final static DataManager INSTANCE = new DataManager();
    private DatabaseReference mDatabase;
    private Event mActiveEvent;

    public interface PlayerAddedCallback {
        public void onPlayerAdded();
    }

    public interface EventFetchedCallback {
        public void onEventFetched(Event activeEvent);
    }

    private DataManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /*mDatabase.child("events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //dataSnapshot.
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public static DataManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the coming(active) event.
     */
    public void getEvent(final EventFetchedCallback callback) {

        if (mActiveEvent != null) {
            callback.onEventFetched(mActiveEvent);
        } else {
            // Get latest event
            mDatabase.child("events").orderByChild("date")
                    .limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        mActiveEvent = messageSnapshot.getValue(Event.class);
                    }

                    callback.onEventFetched(mActiveEvent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("QWER", "error1");
                }
            });
        }
    }

    public ArrayList<EventPlayer> getEventPlayers() {
        return new ArrayList<>(mActiveEvent.players.values());
    }

    public void addEvent(final Event event) {

        // TODO get players
        mDatabase.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                event.players = new HashMap<>();

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Player player = messageSnapshot.getValue(Player.class);
                    EventPlayer eventPlayer = new EventPlayer(player, 0, event.id);
                    event.players.put(player.id, eventPlayer);
                }

                // Create new event
                mDatabase.child("events").child(String.valueOf(event.id)).setValue(event);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("QWER", "error1");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("QWER", "error1");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QWER", "error1");
            }
        });
    }

    public void addPlayer(Event event, Player player) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/players/" + player.id, player);
        childUpdates.put("/events/" + event.id + "/players/" + player.id, new EventPlayer(player, 0, event.id));
        childUpdates.put("/events/" + event.id + "/players/" + player.id, new EventPlayer(player, 0, event.id));

        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.e("qwer", String.valueOf("EROR: " + databaseError == null));
            }
        });
    }


}

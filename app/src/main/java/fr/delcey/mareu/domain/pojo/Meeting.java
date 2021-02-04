package fr.delcey.mareu.domain.pojo;

import java.time.LocalTime;
import java.util.List;

import androidx.annotation.NonNull;

public class Meeting {

    private final int id;

    @NonNull
    private final String topic;

    @NonNull
    private final LocalTime time;

    @NonNull
    private final List<String> participants;

    @NonNull
    private final Room room;

    public Meeting(
        int id,
        @NonNull String topic,
        @NonNull LocalTime time,
        @NonNull List<String> participants,
        @NonNull Room room
    ) {
        this.id = id;
        this.topic = topic;
        this.time = time;
        this.participants = participants;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getTopic() {
        return topic;
    }

    @NonNull
    public LocalTime getTime() {
        return time;
    }

    @NonNull
    public List<String> getParticipants() {
        return participants;
    }

    @NonNull
    public Room getRoom() {
        return room;
    }
}

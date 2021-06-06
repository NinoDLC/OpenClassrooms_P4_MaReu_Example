package fr.delcey.mareu.ui.meetings.room_filter;

import androidx.annotation.NonNull;

import fr.delcey.mareu.data.meeting.model.Room;

public interface OnRoomSelectedListener {
    void onRoomSelected(@NonNull Room room);
}

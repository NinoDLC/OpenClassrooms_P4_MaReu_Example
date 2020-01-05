package fr.delcey.mareu.ui.meetings.room_filter;

import androidx.annotation.NonNull;

import fr.delcey.mareu.domain.pojo.Room;

public interface OnRoomSelectedListener {
    void onRoomSelected(@NonNull Room room);
}

package fr.delcey.mareu.ui.meetings.room_filter;

import androidx.annotation.NonNull;

import fr.delcey.mareu.domain.pojo.Room;

public class RoomFilterItemModel {

    @NonNull
    private final Room room;

    private final boolean isSelected;

    public RoomFilterItemModel(@NonNull Room room, boolean isSelected) {
        this.room = room;
        this.isSelected = isSelected;
    }

    @NonNull
    public Room getRoom() {
        return room;
    }

    public boolean isSelected() {
        return isSelected;
    }
}

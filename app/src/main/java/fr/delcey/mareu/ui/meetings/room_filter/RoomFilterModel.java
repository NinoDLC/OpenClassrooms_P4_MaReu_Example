package fr.delcey.mareu.ui.meetings.room_filter;

import androidx.annotation.NonNull;

import java.util.List;

public class RoomFilterModel {

    @NonNull
    private final List<RoomFilterItemModel> roomFilterItemModels;

    private final boolean isRoomFilterVisible;

    public RoomFilterModel(@NonNull List<RoomFilterItemModel> roomFilterItemModels, boolean isRoomFilterVisible) {
        this.roomFilterItemModels = roomFilterItemModels;
        this.isRoomFilterVisible = isRoomFilterVisible;
    }

    @NonNull
    public List<RoomFilterItemModel> getRoomFilterItemModels() {
        return roomFilterItemModels;
    }

    public boolean isRoomFilterVisible() {
        return isRoomFilterVisible;
    }
}

package fr.delcey.mareu.ui.meetings.room_filter;

import androidx.annotation.NonNull;

import java.util.Objects;

import fr.delcey.mareu.domain.pojo.Room;

public class MeetingViewStateRoomFilterItem {

    @NonNull
    private final Room room;

    private final boolean isSelected;

    public MeetingViewStateRoomFilterItem(@NonNull Room room, boolean isSelected) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingViewStateRoomFilterItem that = (MeetingViewStateRoomFilterItem) o;
        return isSelected == that.isSelected &&
            room == that.room;
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, isSelected);
    }

    @NonNull
    @Override
    public String toString() {
        return "MeetingViewStateRoomFilterItem{" +
            "room=" + room +
            ", isSelected=" + isSelected +
            '}';
    }
}

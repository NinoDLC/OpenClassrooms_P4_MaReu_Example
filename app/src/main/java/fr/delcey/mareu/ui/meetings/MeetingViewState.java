package fr.delcey.mareu.ui.meetings;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import fr.delcey.mareu.ui.meetings.hour_filter.MeetingViewStateHourFilterItem;
import fr.delcey.mareu.ui.meetings.list.MeetingViewStateItem;
import fr.delcey.mareu.ui.meetings.room_filter.MeetingViewStateRoomFilterItem;

public class MeetingViewState {

    @NonNull
    private final List<MeetingViewStateItem> meetingViewStateItems;

    @NonNull
    private final List<MeetingViewStateRoomFilterItem> meetingViewStateRoomFilterItems;

    @NonNull
    private final List<MeetingViewStateHourFilterItem> meetingViewStateHourFilterItems;

    public MeetingViewState(
        @NonNull List<MeetingViewStateItem> meetingViewStateItems,
        @NonNull List<MeetingViewStateRoomFilterItem> meetingViewStateRoomFilterItems,
        @NonNull List<MeetingViewStateHourFilterItem> meetingViewStateHourFilterItems
    ) {
        this.meetingViewStateItems = meetingViewStateItems;
        this.meetingViewStateRoomFilterItems = meetingViewStateRoomFilterItems;
        this.meetingViewStateHourFilterItems = meetingViewStateHourFilterItems;
    }

    @NonNull
    public List<MeetingViewStateItem> getMeetingViewStateItems() {
        return meetingViewStateItems;
    }

    @NonNull
    public List<MeetingViewStateRoomFilterItem> getMeetingViewStateRoomFilterItems() {
        return meetingViewStateRoomFilterItems;
    }

    @NonNull
    public List<MeetingViewStateHourFilterItem> getMeetingViewStateHourFilterItems() {
        return meetingViewStateHourFilterItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingViewState that = (MeetingViewState) o;
        return meetingViewStateItems.equals(that.meetingViewStateItems) &&
            meetingViewStateRoomFilterItems.equals(that.meetingViewStateRoomFilterItems) &&
            meetingViewStateHourFilterItems.equals(that.meetingViewStateHourFilterItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingViewStateItems, meetingViewStateRoomFilterItems, meetingViewStateHourFilterItems);
    }

    @NonNull
    @Override
    public String toString() {
        return "MeetingViewState{" +
            "meetingViewStateItems=" + meetingViewStateItems +
            ", meetingViewStateRoomFilterItems=" + meetingViewStateRoomFilterItems +
            ", meetingViewStateHourFilterItems=" + meetingViewStateHourFilterItems +
            '}';
    }
}

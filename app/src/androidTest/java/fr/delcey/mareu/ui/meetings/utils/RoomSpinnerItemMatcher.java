package fr.delcey.mareu.ui.meetings.utils;

import androidx.annotation.NonNull;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import fr.delcey.mareu.data.meeting.model.Room;

public class RoomSpinnerItemMatcher extends TypeSafeMatcher<Room> {
    private final Room room;

    public RoomSpinnerItemMatcher(@NonNull Room room) {
        this.room = room;
    }

    @Override
    protected boolean matchesSafely(Room item) {
        return item == room;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("RoomSpinnerItemMatcher with Room = " + room);
    }
}

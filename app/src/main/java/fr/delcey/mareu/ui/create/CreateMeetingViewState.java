package fr.delcey.mareu.ui.create;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.delcey.mareu.data.meeting.model.Room;

class CreateMeetingViewState {

    @NonNull
    private final Room[] rooms;

    @NonNull
    private final String time;

    @Nullable
    private final String topicError;

    @Nullable
    private final String participantsError;

    @Nullable
    private final String roomError;

    public CreateMeetingViewState(
        @NonNull Room[] rooms,
        @NonNull String time,
        @Nullable String topicError,
        @Nullable String participantsError,
        @Nullable String roomError
    ) {
        this.rooms = rooms;
        this.time = time;
        this.topicError = topicError;
        this.participantsError = participantsError;
        this.roomError = roomError;
    }

    @NonNull
    public Room[] getRooms() {
        return rooms;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    @Nullable
    public String getTopicError() {
        return topicError;
    }

    @Nullable
    public String getParticipantsError() {
        return participantsError;
    }

    @Nullable
    public String getRoomError() {
        return roomError;
    }
}

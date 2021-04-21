package fr.delcey.mareu.ui.create;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.delcey.mareu.domain.pojo.Room;

class CreateMeetingViewState {

    @NonNull
    private final Room[] spinnerData;

    @Nullable
    private final String topicError;

    @Nullable
    private final String participantsError;

    private final boolean isRoomErrorVisible;

    public CreateMeetingViewState(
        @NonNull Room[] spinnerData,
        @Nullable String topicError,
        @Nullable String participantsError,
        boolean isRoomErrorVisible
    ) {
        this.spinnerData = spinnerData;
        this.topicError = topicError;
        this.participantsError = participantsError;
        this.isRoomErrorVisible = isRoomErrorVisible;
    }

    @NonNull
    public Room[] getSpinnerData() {
        return spinnerData;
    }

    @Nullable
    public String getTopicError() {
        return topicError;
    }

    @Nullable
    public String getParticipantsError() {
        return participantsError;
    }

    public boolean isRoomErrorVisible() {
        return isRoomErrorVisible;
    }
}

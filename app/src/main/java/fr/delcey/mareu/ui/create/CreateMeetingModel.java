package fr.delcey.mareu.ui.create;

import androidx.annotation.Nullable;

class CreateMeetingModel {

    @Nullable
    private final String topicError;

    @Nullable
    private final String participantsError;

    private final boolean isRoomErrorVisible;

    public CreateMeetingModel(
        @Nullable String topicError,
        @Nullable String participantsError,
        boolean isRoomErrorVisible
    ) {
        this.topicError = topicError;
        this.participantsError = participantsError;
        this.isRoomErrorVisible = isRoomErrorVisible;
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

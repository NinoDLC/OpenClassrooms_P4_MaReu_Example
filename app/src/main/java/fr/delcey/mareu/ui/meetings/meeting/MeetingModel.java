package fr.delcey.mareu.ui.meetings.meeting;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class MeetingModel {

    private final int meetingId;

    @DrawableRes
    private final int meetingIcon;

    @NonNull
    private final String title;

    @NonNull
    private final String participants;

    public MeetingModel(
        int meetingId,
        @DrawableRes int meetingIcon,
        @NonNull String title,
        @NonNull String participants
    ) {
        this.meetingId = meetingId;
        this.meetingIcon = meetingIcon;
        this.title = title;
        this.participants = participants;
    }

    public int getMeetingId() {
        return meetingId;
    }

    @DrawableRes
    public int getMeetingIcon() {
        return meetingIcon;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getParticipants() {
        return participants;
    }
}

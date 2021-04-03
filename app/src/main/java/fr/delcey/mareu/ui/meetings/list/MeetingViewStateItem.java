package fr.delcey.mareu.ui.meetings.list;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.Objects;

public class MeetingViewStateItem {

    private final int meetingId;

    @DrawableRes
    private final int meetingIcon;

    @NonNull
    private final String topic;

    @NonNull
    private final String participants;

    public MeetingViewStateItem(
        int meetingId,
        @DrawableRes int meetingIcon,
        @NonNull String topic,
        @NonNull String participants
    ) {
        this.meetingId = meetingId;
        this.meetingIcon = meetingIcon;
        this.topic = topic;
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
    public String getTopic() {
        return topic;
    }

    @NonNull
    public String getParticipants() {
        return participants;
    }

    // TODO NINO better UT with this
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingViewStateItem that = (MeetingViewStateItem) o;
        return meetingId == that.meetingId &&
            meetingIcon == that.meetingIcon &&
            topic.equals(that.topic) &&
            participants.equals(that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingId, meetingIcon, topic, participants);
    }

    @NonNull
    @Override
    public String toString() {
        return "MeetingViewState{" +
            "meetingId=" + meetingId +
            ", meetingIcon=" + meetingIcon +
            ", topic='" + topic + '\'' +
            ", participants='" + participants + '\'' +
            '}';
    }
}

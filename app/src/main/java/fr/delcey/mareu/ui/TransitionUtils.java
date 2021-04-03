package fr.delcey.mareu.ui;

public class TransitionUtils {

    public static String getMeetingRoomTransitionName(int meetingId) {
        return "meeting_room" + meetingId;
    }

    public static String getMeetingTopicTransitionName(int meetingId) {
        return "meeting_topic" + meetingId;
    }
}

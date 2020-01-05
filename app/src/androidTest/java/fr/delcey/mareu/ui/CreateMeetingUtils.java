package fr.delcey.mareu.ui;

import androidx.annotation.NonNull;
import androidx.test.espresso.contrib.PickerActions;

import org.threeten.bp.LocalTime;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.meetings.utils.RoomSpinnerItemMatcher;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class CreateMeetingUtils {

    public static void createMeeting(
        @NonNull final String topic,
        @NonNull final String participants,
        @NonNull final Room room,
        @NonNull final LocalTime time
    ) {
        setMeetingTopic(topic);

        setMeetingParticipants(participants);

        setMeetingRoom(room);

        onView(withId(R.id.create_meeting_tp)).perform(PickerActions.setTime(time.getHour(), time.getMinute()));
    }

    public static void setMeetingTopic(@NonNull String topic) {
        onView(
            withId(R.id.create_meeting_et_topic)
        ).perform(
            scrollTo(),
            replaceText(topic),
            closeSoftKeyboard()
        );
    }

    public static void setMeetingParticipants(@NonNull String participants) {
        onView(
            withId(R.id.create_meeting_et_participants)
        ).perform(
            scrollTo(),
            replaceText(participants),
            closeSoftKeyboard()
        );
    }

    public static void setMeetingRoom(@NonNull Room room) {
        onView(withId(R.id.create_meeting_spi_room)).perform(scrollTo(), click());

        onData(new RoomSpinnerItemMatcher(room)).perform(scrollTo(), click());
    }
}

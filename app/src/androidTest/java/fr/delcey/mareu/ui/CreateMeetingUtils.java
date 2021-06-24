package fr.delcey.mareu.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.model.Room;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class CreateMeetingUtils {

    /**
     * Creates a meeting, given we are on the CreateMeetingFragment. Provide a null argument to ignore
     *
     * @param topic        if null, won't change the current topic, else, change it
     * @param participants if null, won't change the current participants, else, change it
     * @param room         if null, won't change the current room, else, change it
     * @param time         if null, won't change the current time, else, change it
     */
    public static void createMeeting(
        @Nullable final String topic,
        @Nullable final String participants,
        @Nullable final Room room,
        @Nullable final LocalTime time
    ) {
        if (topic != null) {
            setMeetingTopic(topic);
        }

        if (participants != null) {
            setMeetingParticipants(participants);
        }

        if (room != null) {
            setMeetingRoom(room);
        }

        if (time != null) {
            setMeetingTime(time);
        }
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
        onView(withId(R.id.create_meeting_til_room)).perform(scrollTo(), click());

        onData(is(room)).inRoot(isPlatformPopup()).perform(scrollTo(), click());
    }

    public static void setMeetingTime(@NonNull LocalTime time) {
        if (time.getHour() % 2 != 0) {
            throw new IllegalStateException("Sorry, this method can't work with odd hours. Gotta improve it !");
        }

        if (time.getMinute() % 5 != 0) {
            throw new IllegalStateException("Sorry, this method can't work with minutes not divisible by 5. Gotta improve it !");
        }

        onView(withId(R.id.create_meeting_til_time)).perform(scrollTo(), click());

        onView(
            allOf(
                withText("" + time.getHour()),
                withParent(withId(R.id.material_clock_face))
            )
        ).perform(click());

        onView(
            allOf(
                withText("" + time.getMinute()),
                withParent(withId(R.id.material_clock_face))
            )
        ).perform(click());

        onView(withId(R.id.material_timepicker_ok_button)).perform(click());
    }
}

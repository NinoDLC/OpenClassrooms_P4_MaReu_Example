package fr.delcey.mareu.ui.create;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.ui.CreateMeetingUtils;
import fr.delcey.mareu.ui.utils.EditTextErrorMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.hasImeAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateMeetingActivityTest {

    private CreateMeetingActivity activityRef;

    @Before
    public void setUp() {
        ActivityScenario<CreateMeetingActivity> activityScenario = ActivityScenario.launch(CreateMeetingActivity.class);
        activityScenario.onActivity(activity -> activityRef = activity);
    }

    @Test
    public void createMeeting() {
        // Never put time as field... always local variables (or access time on-execution) !
        LocalTime localTime = LocalTime.of(8, 30);

        CreateMeetingUtils.createMeeting("topic", "foo.bar@gmail.com", Room.PEACH, localTime);

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertTrue(activityRef.isFinishing());
    }

    @Test
    public void assert_ime_options() {
        InputMethodManager inputMethodManager = (InputMethodManager) InstrumentationRegistry.getInstrumentation().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assertNotNull(inputMethodManager);

        // Keyboard is not visible at start of Activity
        assertFalse(inputMethodManager.isAcceptingText());

        onView(withId(R.id.create_meeting_et_topic)).perform(scrollTo(), click());

        // Keyboard is visible when clicking topic edittext
        assertTrue(inputMethodManager.isAcceptingText());

        onView(withId(R.id.create_meeting_et_topic)).check(matches(hasFocus()));
        onView(withId(R.id.create_meeting_et_topic)).check(matches(hasImeAction(EditorInfo.IME_ACTION_NEXT)));
        onView(withId(R.id.create_meeting_et_topic)).perform(pressImeActionButton());

        // Keyboard is still visible when clicking topic edittext's ime option (next)
        assertTrue(inputMethodManager.isAcceptingText());

        onView(withId(R.id.create_meeting_et_participants)).check(matches(hasFocus()));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(hasImeAction(EditorInfo.IME_ACTION_DONE)));
        onView(withId(R.id.create_meeting_et_participants)).perform(pressImeActionButton());
    }

    @Test
    public void assert_user_input_checks_before_creating_meeting() {
        CreateMeetingUtils.createMeeting("", "", null, null);

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertFalse(activityRef.isFinishing());
        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(R.string.topic_user_input_error)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(R.string.participants_user_input_error)));
        onView(withId(R.id.create_meeting_actv_room)).check(matches(new EditTextErrorMatcher(R.string.room_user_input_error)));
        onView(withId(R.id.create_meeting_et_time)).check(matches(not(withText(""))));

        CreateMeetingUtils.setMeetingTopic("topic");

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertFalse(activityRef.isFinishing());
        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(R.string.participants_user_input_error)));
        onView(withId(R.id.create_meeting_actv_room)).check(matches(new EditTextErrorMatcher(R.string.room_user_input_error)));
        onView(withId(R.id.create_meeting_et_time)).check(matches(not(withText(""))));

        CreateMeetingUtils.setMeetingParticipants("foo.bar@gmail.com");

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertFalse(activityRef.isFinishing());
        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_actv_room)).check(matches(new EditTextErrorMatcher(R.string.room_user_input_error)));
        onView(withId(R.id.create_meeting_et_time)).check(matches(not(withText(""))));

        CreateMeetingUtils.setMeetingRoom(Room.PEACH);

        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_actv_room)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_et_time)).check(matches(not(withText(""))));

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());
        assertTrue(activityRef.isFinishing());
    }
}

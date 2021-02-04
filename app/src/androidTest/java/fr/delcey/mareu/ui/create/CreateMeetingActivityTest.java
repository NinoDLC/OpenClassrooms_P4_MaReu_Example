package fr.delcey.mareu.ui.create;


import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.CreateMeetingUtils;
import fr.delcey.mareu.ui.utils.EditTextErrorMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.hasImeAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateMeetingActivityTest {

    @Rule
    public final ActivityTestRule<CreateMeetingActivity> activityTestRule = new ActivityTestRule<>(CreateMeetingActivity.class);

    @Test
    public void createMeeting() {
        // Never put time as field... always local variables (or access time on-execution) !
        LocalTime localTime = LocalTime.of(8, 30);

        CreateMeetingUtils.createMeeting("topic", "foo.bar@gmail.com", Room.PEACH, localTime);

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertTrue(activityTestRule.getActivity().isFinishing());
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
        // Never put time as field... always local variables (or access time on-execution) !
        LocalTime firstTime = LocalTime.of(8, 30);

        CreateMeetingUtils.createMeeting("", "", Room.UNKNOW, firstTime);

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertFalse(activityTestRule.getActivity().isFinishing());
        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(R.string.topic_user_input_error)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(R.string.participants_user_input_error)));
        onView(withId(R.id.create_meeting_tv_room_error)).check(matches(isDisplayed()));

        CreateMeetingUtils.setMeetingTopic("topic");

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertFalse(activityTestRule.getActivity().isFinishing());
        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(R.string.participants_user_input_error)));
        onView(withId(R.id.create_meeting_tv_room_error)).check(matches(isDisplayed()));

        CreateMeetingUtils.setMeetingParticipants("foo.bar@gmail.com");

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());

        assertFalse(activityTestRule.getActivity().isFinishing());
        onView(withId(R.id.create_meeting_et_topic)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_et_participants)).check(matches(new EditTextErrorMatcher(0)));
        onView(withId(R.id.create_meeting_tv_room_error)).check(matches(isDisplayed()));

        CreateMeetingUtils.setMeetingRoom(Room.PEACH);

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());
        assertTrue(activityTestRule.getActivity().isFinishing());
    }
}

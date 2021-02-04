package fr.delcey.mareu.ui.meetings;


import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.CreateMeetingUtils;
import fr.delcey.mareu.ui.meetings.utils.HourFilterViewHolderMatcher;
import fr.delcey.mareu.ui.meetings.utils.RoomFilterViewHolderMatcher;
import fr.delcey.mareu.ui.utils.ClickChildViewWithId;
import fr.delcey.mareu.ui.utils.DrawableMatcher;
import fr.delcey.mareu.ui.utils.RecyclerViewItemAssertion;
import fr.delcey.mareu.ui.utils.RecyclerViewItemCountAssertion;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static fr.delcey.mareu.domain.pojo.Room.DK;
import static fr.delcey.mareu.domain.pojo.Room.MARIO;
import static fr.delcey.mareu.domain.pojo.Room.PEACH;
import static fr.delcey.mareu.domain.pojo.Room.YOSHI;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MeetingActivityTest {

    public static final String FIRST_TOPIC = "FIRST_TOPIC";
    public static final String FIRST_PARTICIPANTS = "foo.bar@gmail.com";
    public static final Room FIRST_ROOM = MARIO;

    private static final String SECOND_TOPIC = "SECOND_TOPIC";
    private static final String SECOND_PARTICIPANTS = "toto.tata@gmail.com, foo.bar@gmail.com";
    private static final Room SECOND_ROOM = PEACH;

    private static final String THIRD_TOPIC = "THIRD_TOPIC";
    private static final String THIRD_PARTICIPANTS = "john.smith@outlook.com, john.doe@outlook.com, one.random_guy@123consulting.biz";
    private static final Room THIRD_ROOM = DK;

    private static final String FOURTH_TOPIC = "FOURTH_TOPIC";
    private static final String FOURTH_PARTICIPANTS = "sherlock.holmes@watson.home";
    private static final Room FOURTH_ROOM = PEACH;

    private static final String FIFTH_TOPIC = "FIFTH_TOPIC";
    private static final String FIFTH_PARTICIPANTS = "count.dracula@hoteltransylvania.travel, mavis.dracula@hoteltransylvania.travel, wayne@woof.yahoo";
    private static final Room FIFTH_ROOM = YOSHI;

    private MeetingActivity activityRef;

    @Before
    public void setUp() {
        ActivityScenario<MeetingActivity> activityScenario = ActivityScenario.launch(MeetingActivity.class);
        activityScenario.recreate();
        activityScenario.onActivity(activity -> activityRef = activity);
    }

    @After
    public void tearDown() {
        activityRef = null;
    }

    @Test
    public void createMultipleMeetingsWithFilteringAndSorting() throws InterruptedException {
        // TODO NINO This is so hacky but ActivityScenario don't permit the use of the Orchestrator yet (to run "pm clear" command)...
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
            }
        } catch (PerformException ignored) {
            // Happens when all integration tests are launched sequentially, we remove the 2 "Meetings" before
        }

        // Never put time as field... always local variables (or access time on-execution) !
        LocalTime firstTime = LocalTime.of(8, 30);
        LocalTime secondTime = LocalTime.of(16, 15);
        LocalTime thirdTime = LocalTime.of(12, 15);
        LocalTime fourthTime = LocalTime.of(13, 45);
        LocalTime fifthTime = LocalTime.of(17, 0);

        // Action : Create first meeting
        createMeeting(FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Go on "Create meeting page" and back immediately
        onView(withId(R.id.meeting_fab)).perform(click());
        Espresso.pressBack();

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Create second meeting
        createMeeting(SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : Delete meeting 1 (Mario)
        onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
        // TODO NINO WHY IS IT NECESSARY ?
        Thread.sleep(100);

        // Assertions : Mario is deleted
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : (Re)create first meeting
        createMeeting(FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Assertions : Mario is second in list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Delete meeting 2 (Peach)
        onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
        // TODO NINO WHY IS IT NECESSARY ?
        Thread.sleep(100);

        // Assertions : Mario is alone in the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : (Re)create second meeting
        createMeeting(SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // region FILTERS
        /* ***********
         *  FILTERS  *
         *********** */
        RoomFilterViewHolderMatcher marioRoomFilterViewHolderMatcher = new RoomFilterViewHolderMatcher(MARIO.getStringResName());
        RoomFilterViewHolderMatcher peachRoomFilterViewHolderMatcher = new RoomFilterViewHolderMatcher(PEACH.getStringResName());
        RoomFilterViewHolderMatcher dkRoomFilterViewHolderMatcher = new RoomFilterViewHolderMatcher(DK.getStringResName());
        RoomFilterViewHolderMatcher yoshiRoomFilterViewHolderMatcher = new RoomFilterViewHolderMatcher(YOSHI.getStringResName());

        HourFilterViewHolderMatcher hour0600FilterViewHolderMatcher = new HourFilterViewHolderMatcher("06:00");
        HourFilterViewHolderMatcher hour0800FilterViewHolderMatcher = new HourFilterViewHolderMatcher("08:00");
        HourFilterViewHolderMatcher hour1200FilterViewHolderMatcher = new HourFilterViewHolderMatcher("12:00");
        HourFilterViewHolderMatcher hour1600FilterViewHolderMatcher = new HourFilterViewHolderMatcher("16:00");

        // Action : display room filter
        onView(withId(R.id.meeting_menu_filter_room)).perform(click());

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : Filter on MARIO only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : display hour filter
        onView(withId(R.id.meeting_menu_filter_hour)).perform(click());

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Filter on 08:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0800FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0800FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Filter on 06:00 & 08:00
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0600FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Filter on 06:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0800FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0800FilterViewHolderMatcher, click()));

        // Assertions : No one is on the list (who the fuck wakes up so early ?)
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Remove 06:00 filter
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0600FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Remove MARIO filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : Filter on PEACH only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : Remove PEACH filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : Filter on DK only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : There is no DK meeting (yet)
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Filter on 12:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : There is no DK meeting (yet)
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Create third meeting
        createMeeting(THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Assertions : DK is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Remove DK filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Filter on 12:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Filter on DK only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Remove DK filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Filter on PEACH...
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : (Filter on PEACH...) & DK
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Filter on 12:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Create fourth meeting
        createMeeting(FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Assertions : One PEACH & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(1, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Remove 12:00 filter
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : PEACH (x2) & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Remove PEACH filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Filter on PEACH...
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH (x2) & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Create fifth meeting
        createMeeting(FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);

        // Assertions : PEACH (x2) & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Delete meeting 4 (second Peach)
        onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(2, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
        // TODO NINO WHY IS IT NECESSARY ?
        Thread.sleep(100);

        // Assertions : PEACH & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);

        // Action : Add YOSHI filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(yoshiRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(yoshiRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH, DK & YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(2, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);

        // Action : (Re)create fourth meeting
        createMeeting(FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Assertions : PEACH, DK & YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(4));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(2, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);
        assertStateForItem(3, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Remove DK filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH (x2) & YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Remove PEACH filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : YOSHI is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);

        // Action : Remove YOSHI filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(yoshiRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(yoshiRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(5));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(3, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);
        assertStateForItem(4, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);

        // Action : Filter on 16:00
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1600FilterViewHolderMatcher, click()));

        // Assertions : One PEACH and YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);

        // Action : Open sorting pop-up
        onView(withId(R.id.meeting_menu_sort)).perform(click());

        // Assertions : Basic dialog state
        onView(withId(R.id.meeting_sorting_dialog_tv_alphabetical)).check(matches(withText(R.string.sorting_alphabetic_none)));
        onView(withId(R.id.meeting_sorting_dialog_tv_chronological)).check(matches(withText(R.string.sorting_chronological_none)));

        // Action : Sort by alphabetical
        onView(withId(R.id.meeting_sorting_dialog_ll_alphabetical)).perform(click());

        // Assertions : Alphabetical dialog state
        onView(withId(R.id.meeting_sorting_dialog_tv_alphabetical)).check(matches(withText(R.string.sorting_alphabetic_sorted)));
        onView(withId(R.id.meeting_sorting_dialog_tv_chronological)).check(matches(withText(R.string.sorting_chronological_none)));

        // Action : Back to MeetingActivity
        Espresso.pressBack();

        // Assertions : YOSHI and one PEACH are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);

        // Action : Open sorting pop-up
        onView(withId(R.id.meeting_menu_sort)).perform(click());

        // Assertions : Alphabetical dialog state
        onView(withId(R.id.meeting_sorting_dialog_tv_alphabetical)).check(matches(withText(R.string.sorting_alphabetic_sorted)));
        onView(withId(R.id.meeting_sorting_dialog_tv_chronological)).check(matches(withText(R.string.sorting_chronological_none)));

        // Action : Sort by alphabetical
        onView(withId(R.id.meeting_sorting_dialog_ll_alphabetical)).perform(click());

        // Assertions : Invert alphabetical dialog state
        onView(withId(R.id.meeting_sorting_dialog_tv_alphabetical)).check(matches(withText(R.string.sorting_alphabetic_inverted_sorted)));
        onView(withId(R.id.meeting_sorting_dialog_tv_chronological)).check(matches(withText(R.string.sorting_chronological_none)));

        // Action : Back to MeetingActivity
        Espresso.pressBack();

        // Assertions : One PEACH and YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(1, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);

        // Action : Filter on MARIO only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : No one is on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Remove 16:00 filter
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1600FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);

        // Action : Remove MARIO filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : Inverted alphabetical sorting
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(5));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);
        assertStateForItem(3, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(4, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);

        // Action : Open sorting pop-up
        onView(withId(R.id.meeting_menu_sort)).perform(click());

        // Assertions : Inverted alphabetical dialog state
        onView(withId(R.id.meeting_sorting_dialog_tv_alphabetical)).check(matches(withText(R.string.sorting_alphabetic_inverted_sorted)));
        onView(withId(R.id.meeting_sorting_dialog_tv_chronological)).check(matches(withText(R.string.sorting_chronological_none)));

        // Action : Sort by none
        onView(withId(R.id.meeting_sorting_dialog_ll_alphabetical)).perform(click());

        // Assertions : Basic dialog state
        onView(withId(R.id.meeting_sorting_dialog_tv_alphabetical)).check(matches(withText(R.string.sorting_alphabetic_none)));
        onView(withId(R.id.meeting_sorting_dialog_tv_chronological)).check(matches(withText(R.string.sorting_chronological_none)));

        // Action : Back to MeetingActivity
        Espresso.pressBack();

        // Assertions : Back to "normal"
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, firstTime);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, secondTime);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, thirdTime);
        assertStateForItem(3, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, fifthTime);
        assertStateForItem(4, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, fourthTime);
        // endregion
    }

    private void assertStateForItem(
        int positionOnRecyclerView,
        @NonNull String topic,
        @NonNull String participants,
        @NonNull Room room,
        @NonNull LocalTime time
    ) {
        onView(withId(R.id.meeting_rv)).check(
            new RecyclerViewItemAssertion(
                positionOnRecyclerView,
                R.id.meeting_item_iv_room,
                new DrawableMatcher(room.getDrawableResIcon())
            )
        );
        onView(withId(R.id.meeting_rv)).check(
            new RecyclerViewItemAssertion(
                positionOnRecyclerView,
                R.id.meeting_item_tv_title,
                withText(topic + " - " + time.toString() + " - " + activityRef.getString(room.getStringResName()))
            )
        );
        onView(withId(R.id.meeting_rv)).check(
            new RecyclerViewItemAssertion(
                positionOnRecyclerView,
                R.id.meeting_item_tv_participants,
                withText(participants)
            )
        );
    }

    private void createMeeting(
        @NonNull final String topic,
        @NonNull final String participants,
        @NonNull final Room room,
        @NonNull final LocalTime time
    ) {
        onView(withId(R.id.meeting_fab)).perform(click());

        CreateMeetingUtils.createMeeting(topic, participants, room, time);

        onView(withId(R.id.create_meeting_fab_validate)).perform(click());
    }
}

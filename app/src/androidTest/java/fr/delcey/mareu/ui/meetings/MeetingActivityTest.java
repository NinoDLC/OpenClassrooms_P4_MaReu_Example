package fr.delcey.mareu.ui.meetings;


import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
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

    private static final String FIRST_TOPIC = "FIRST_TOPIC";
    private static final String FIRST_PARTICIPANTS = "foo.bar@gmail.com";
    private static final Room FIRST_ROOM = MARIO;
    private static final LocalTime FIRST_TIME = LocalTime.of(8, 30);

    private static final String SECOND_TOPIC = "SECOND_TOPIC";
    private static final String SECOND_PARTICIPANTS = "toto.tata@gmail.com, foo.bar@gmail.com";
    private static final Room SECOND_ROOM = PEACH;
    private static final LocalTime SECOND_TIME = LocalTime.of(16, 15);

    private static final String THIRD_TOPIC = "THIRD_TOPIC";
    private static final String THIRD_PARTICIPANTS = "john.smith@outlook.com, john.doe@outlook.com, one.random_guy@123consulting.biz";
    private static final Room THIRD_ROOM = DK;
    private static final LocalTime THIRD_TIME = LocalTime.of(12, 15);

    private static final String FOURTH_TOPIC = "FOURTH_TOPIC";
    private static final String FOURTH_PARTICIPANTS = "sherlock.holmes@watson.home";
    private static final Room FOURTH_ROOM = PEACH;
    private static final LocalTime FOURTH_TIME = LocalTime.of(12, 45);

    private static final String FIFTH_TOPIC = "FIFTH_TOPIC";
    private static final String FIFTH_PARTICIPANTS = "count.dracula@hoteltransylvania.travel, mavis.dracula@hoteltransylvania.travel, wayne@woof.yahoo";
    private static final Room FIFTH_ROOM = YOSHI;
    private static final LocalTime FIFTH_TIME = LocalTime.of(17, 0);

    private MeetingActivity activityRef;

    @Before
    public void setUp() {
        ActivityScenario<MeetingActivity> activityScenario = ActivityScenario.launch(MeetingActivity.class);
        activityScenario.onActivity(activity -> activityRef = activity);
    }

    @After
    public void tearDown() {
        activityRef = null;
    }

    @Test
    public void createMultipleMeetingsWithFilteringAndSorting() throws InterruptedException {

        // Action : Create first meeting
        createMeeting(FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Assertions : Detail screen for Mario
        assertDetailForItemAtPosition(0, FIRST_TOPIC, FIRST_ROOM);

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Go on "Create meeting page" and back immediately
        onView(withId(R.id.meeting_fab)).perform(click());
        pressBack();

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Create second meeting
        createMeeting(SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Assertions : Detail screen for Mario
        assertDetailForItemAtPosition(0, FIRST_TOPIC, FIRST_ROOM);

        // Assertions : Detail screen for Peach
        assertDetailForItemAtPosition(1, SECOND_TOPIC, SECOND_ROOM);

        // Action : Delete meeting 1 (Mario)
        onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
        Thread.sleep(100);

        // Assertions : Mario is deleted
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Assertions : Detail screen for Peach
        assertDetailForItemAtPosition(0, SECOND_TOPIC, SECOND_ROOM);

        // Action : (Re)create first meeting
        createMeeting(FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Assertions : Mario is second in list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Assertions : Detail screen for Mario
        assertDetailForItemAtPosition(1, FIRST_TOPIC, FIRST_ROOM);

        // Action : Delete meeting 2 (Peach)
        onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
        Thread.sleep(100);

        // Assertions : Mario is alone in the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : (Re)create second meeting
        createMeeting(SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Assertions : Detail screen for Peach
        assertDetailForItemAtPosition(1, SECOND_TOPIC, SECOND_ROOM);

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
        HourFilterViewHolderMatcher hour1700FilterViewHolderMatcher = new HourFilterViewHolderMatcher("17:00");

        // Action : display room filter
        onView(withId(R.id.meeting_menu_filter_room)).perform(click());

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Action : Filter on MARIO only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : display hour filter
        onView(withId(R.id.meeting_menu_filter_hour)).perform(click());

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Filter on 08:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0800FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0800FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Filter on 06:00 & 08:00
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0600FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Filter on 06:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0800FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0800FilterViewHolderMatcher, click()));

        // Assertions : No one is on the list (who the fuck wakes up so early ?)
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Remove 06:00 filter
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour0600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour0600FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Remove MARIO filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Action : Filter on PEACH only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Action : Remove PEACH filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Action : Filter on DK only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : There is no DK meeting (yet)
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Filter on 12:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : There is no DK meeting (yet)
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Create third meeting
        createMeeting(THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Assertions : DK is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Assertions : Detail screen for DK
        assertDetailForItemAtPosition(0, THIRD_TOPIC, THIRD_ROOM);

        // Action : Remove DK filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Filter on 12:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Filter on DK only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Remove DK filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Filter on PEACH...
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Action : (Filter on PEACH...) & DK
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Filter on 12:00 only
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Create fourth meeting
        createMeeting(FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Assertions : One PEACH & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(1, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Assertions : Detail screen for DK
        assertDetailForItemAtPosition(1, FOURTH_TOPIC, FOURTH_ROOM);

        // Action : Remove 12:00 filter
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1200FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1200FilterViewHolderMatcher, click()));

        // Assertions : PEACH (x2) & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Action : Remove PEACH filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : DK is alone on the list
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Filter on PEACH...
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH (x2) & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Action : Create fifth meeting
        createMeeting(FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);

        // Assertions : PEACH (x2) & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Action : Delete meeting 4 (second Peach)
        onView(withId(R.id.meeting_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(2, new ClickChildViewWithId(R.id.meeting_item_iv_delete)));
        Thread.sleep(100);

        // Assertions : PEACH & DK are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);

        // Action : Add YOSHI filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(yoshiRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(yoshiRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH, DK & YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(2, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);

        // Assertions : Detail screen for Yoshi
        assertDetailForItemAtPosition(2, FIFTH_TOPIC, FIFTH_ROOM);

        // Action : (Re)create fourth meeting
        createMeeting(FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Assertions : PEACH (x2), DK & YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(4));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(2, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);
        assertStateForItem(3, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Assertions : Detail screen for Peach (2)
        assertDetailForItemAtPosition(3, FOURTH_TOPIC, FOURTH_ROOM);

        // Action : Remove DK filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(dkRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(dkRoomFilterViewHolderMatcher, click()));

        // Assertions : PEACH (x2) & YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(3));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Action : Remove PEACH filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(peachRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(peachRoomFilterViewHolderMatcher, click()));

        // Assertions : YOSHI is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);

        // Action : Remove YOSHI filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(yoshiRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(yoshiRoomFilterViewHolderMatcher, click()));

        // Assertions : Back to "normal"
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(5));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(3, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);
        assertStateForItem(4, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);

        // Action : Filter on 16:00
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1600FilterViewHolderMatcher, click()));

        // Assertions : One PEACH is on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

        // Action : Filter on 17:00
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1700FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1700FilterViewHolderMatcher, click()));

        // Assertions : One PEACH and YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);

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
        pressBack();

        // Assertions : YOSHI and one PEACH are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);

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
        pressBack();

        // Assertions : One PEACH and YOSHI are on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(2));
        assertStateForItem(0, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(1, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);

        // Action : Filter on MARIO only
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : No one is on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Action : Remove 16:00 and 17:00 filter
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1600FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1600FilterViewHolderMatcher, click()));
        onView(withId(R.id.meeting_rv_hours)).perform(scrollToHolder(hour1700FilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(hour1700FilterViewHolderMatcher, click()));

        // Assertions : MARIO is alone on the list
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(1));
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);

        // Action : Remove MARIO filter
        onView(withId(R.id.meeting_rv_rooms)).perform(scrollToHolder(marioRoomFilterViewHolderMatcher), RecyclerViewActions.actionOnHolderItem(marioRoomFilterViewHolderMatcher, click()));

        // Assertions : Inverted alphabetical sorting
        onView(withId(R.id.meeting_rv)).check(new RecyclerViewItemCountAssertion(5));
        assertStateForItem(0, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(2, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);
        assertStateForItem(3, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(4, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);

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
        pressBack();

        // Assertions : Back to "normal"
        assertStateForItem(0, FIRST_TOPIC, FIRST_PARTICIPANTS, FIRST_ROOM, FIRST_TIME);
        assertStateForItem(1, SECOND_TOPIC, SECOND_PARTICIPANTS, SECOND_ROOM, SECOND_TIME);
        assertStateForItem(2, THIRD_TOPIC, THIRD_PARTICIPANTS, THIRD_ROOM, THIRD_TIME);
        assertStateForItem(3, FIFTH_TOPIC, FIFTH_PARTICIPANTS, FIFTH_ROOM, FIFTH_TIME);
        assertStateForItem(4, FOURTH_TOPIC, FOURTH_PARTICIPANTS, FOURTH_ROOM, FOURTH_TIME);
        // endregion
    }

    private void assertDetailForItemAtPosition(int position, @NonNull String topic, @NonNull Room room) {
        onView(withId(R.id.meeting_rv)).perform(actionOnItemAtPosition(position, click()));
        onView(withId(R.id.detail_meeting_iv_room)).check(matches(new DrawableMatcher(room.getDrawableResIcon())));
        onView(withId(R.id.detail_meeting_tv_topic)).check(matches(withText(topic)));
        pressBack();
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
                R.id.meeting_item_tv_topic,
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

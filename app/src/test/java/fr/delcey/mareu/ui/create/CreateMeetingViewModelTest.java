package fr.delcey.mareu.ui.create;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.MeetingRepository;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.ui.create.CreateMeetingViewModel.ViewAction;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CreateMeetingViewModelTest {

    private static final String EXPECTED_TOPIC_USER_INPUT_ERROR = "EXPECTED_TOPIC_USER_INPUT_ERROR";
    private static final String EXPECTED_PARTICIPANTS_USER_INPUT_ERROR = "EXPECTED_PARTICIPANTS_USER_INPUT_ERROR";
    private static final String EXPECTED_ROOM_USER_INPUT_ERROR = "EXPECTED_ROOM_USER_INPUT_ERROR";

    private static final String EXPECTED_TIME = "13:00";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Resources resources;

    @Mock
    private MeetingRepository repository;

    private Clock clock;

    private CreateMeetingViewModel viewModel;

    @Before
    public void setUp() {
        // Useful for unit testing with time
        clock = getDefaultClock(getDefaultLocalDate(), getDefaultLocalTime());

        given(resources.getString(R.string.topic_user_input_error)).willReturn(EXPECTED_TOPIC_USER_INPUT_ERROR);
        given(resources.getString(R.string.participants_user_input_error)).willReturn(EXPECTED_PARTICIPANTS_USER_INPUT_ERROR);
        given(resources.getString(R.string.room_user_input_error)).willReturn(EXPECTED_ROOM_USER_INPUT_ERROR);

        viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
    }

    @Test
    public void at_startup_viewmodel_should_expose_rounded_time_and_rooms() {
        // When
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertArrayEquals(Room.values(), result.getRooms());
        assertEquals(EXPECTED_TIME, result.getTime());
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());
    }

    @Test
    public void given_inputs_are_corrects_livedata_should_not_expose_anything_and_view_action_should_expose_close() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.createMeeting();

        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.CloseActivity);
        assertArrayEquals(Room.values(), result.getRooms());
        assertEquals(EXPECTED_TIME, result.getTime());
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());
        verifyRepoAddedMeeting();
    }

    @Test
    public void given_inputs_are_corrects_with_modified_time_livedata_should_not_expose_anything_and_view_action_should_expose_close() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.onTimeChanged(21, 10);
        viewModel.createMeeting();

        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.CloseActivity);
        assertArrayEquals(Room.values(), result.getRooms());
        assertEquals("21:10", result.getTime());
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());
        verify(repository).addMeeting(
            eq("Topic"),
            eq(LocalTime.of(21, 10)),
            eq(new ArrayList<String>() {{
                add("participant@mail.org");
            }}),
            eq(Room.MARIO)
        );
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_current_time_minutes_are_0_then_should_expose_1200() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12,0));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("12:00", result.getTime());
    }

    @Test
    public void given_current_time_minutes_are_1_then_should_expose_1230() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12,1));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("12:30", result.getTime());
    }

    @Test
    public void given_current_time_minutes_are_15_then_should_expose_1230() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12,15));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("12:30", result.getTime());
    }

    @Test
    public void given_current_time_minutes_are_29_then_should_expose_1230() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12, 29));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("12:30", result.getTime());
    }

    @Test
    public void given_current_time_minutes_are_30_then_should_expose_1230() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12, 30));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("12:30", result.getTime());
    }

    @Test
    public void given_current_time_minutes_are_31_then_should_expose_1300() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12, 31));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("13:00", result.getTime());
    }

    @Test
    public void given_current_time_minutes_are_59_then_should_expose_1300() {
        // Given
        clock = getDefaultClock(getDefaultLocalDate(), LocalTime.of(12, 31));

        // When
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel(resources, repository, DateTimeFormatter.ofPattern("HH:mm"), clock);
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals("13:00", result.getTime());
    }

    @Test
    public void given_inputs_are_corrects_with_multiple_participants_livedata_should_not_expose_anything_and_view_action_should_expose_close() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onParticipantsChanged("participant@mail.org,");
        viewModel.onParticipantsChanged("participant@mail.org,   participant2@mail.org  ");
        viewModel.onParticipantsChanged("participant@mail.org,   participant2@mail.org  ,");
        viewModel.onParticipantsChanged("participant@mail.org,   participant2@mail.org  ,     toto@tata.fr, john.smith@consulting.com    ");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.createMeeting();

        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());
        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.CloseActivity);
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());
        verify(repository).addMeeting(
            eq("Topic"),
            eq(LocalTime.of(13, 0)),
            eq(new ArrayList<String>() {{
                add("participant@mail.org");
                add("participant2@mail.org");
                add("toto@tata.fr");
                add("john.smith@consulting.com");
            }}),
            eq(Room.MARIO)
        );
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_topic_is_not_set_livedata_should_expose_viewmodel_with_topic_error() {
        // When
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(EXPECTED_TOPIC_USER_INPUT_ERROR, result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_topic_is_set_then_deleted_livedata_should_expose_viewmodel_with_topic_error() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.onTopicChanged("");
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(EXPECTED_TOPIC_USER_INPUT_ERROR, result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_participants_are_not_set_livedata_should_expose_viewmodel_with_participant_error() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertNull(result.getTopicError());
        assertEquals(EXPECTED_PARTICIPANTS_USER_INPUT_ERROR, result.getParticipantsError());
        assertNull(result.getRoomError());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_participants_are_set_then_deleted_livedata_should_expose_viewmodel_with_participants_error() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.onParticipantsChanged("");
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertNull(result.getTopicError());
        assertEquals(EXPECTED_PARTICIPANTS_USER_INPUT_ERROR, result.getParticipantsError());
        assertNull(result.getRoomError());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_room_is_not_set_livedata_should_expose_viewmodel_with_room_error() {
        // When
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertEquals(EXPECTED_ROOM_USER_INPUT_ERROR, result.getRoomError());
        verifyNoInteractions(repository);
    }

    @Test
    public void verify_click_on_time_edit_text_show_time_picker() {
        // When
        viewModel.onTimeEditTextClicked();
        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.DisplayTimePicker);
        assertEquals(new ViewAction.DisplayTimePicker(13, 0), viewActionResult);
    }

    @Test
    public void given_time_has_changed_verify_click_on_time_edit_text_show_time_picker() {
        // Given
        viewModel.onTimeChanged(17, 50);

        // When
        viewModel.onTimeEditTextClicked();
        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.DisplayTimePicker);
        assertEquals(new ViewAction.DisplayTimePicker(17, 50), viewActionResult);
    }

    @Test
    public void given_nothing_is_set_livedata_should_expose_viewmodel_with_all_errors() {
        // When
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(EXPECTED_TOPIC_USER_INPUT_ERROR, result.getTopicError());
        assertEquals(EXPECTED_PARTICIPANTS_USER_INPUT_ERROR, result.getParticipantsError());
        assertEquals(EXPECTED_ROOM_USER_INPUT_ERROR, result.getRoomError());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_nothing_is_set_then_topic_is_set_livedata_should_expose_viewmodel_with_some_errors() {
        // When
        viewModel.createMeeting();

        CreateMeetingViewState firstResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(EXPECTED_TOPIC_USER_INPUT_ERROR, firstResult.getTopicError());
        assertEquals(EXPECTED_PARTICIPANTS_USER_INPUT_ERROR, firstResult.getParticipantsError());
        assertEquals(EXPECTED_ROOM_USER_INPUT_ERROR, firstResult.getRoomError());

        // When 2
        viewModel.onTopicChanged("Topic");
        viewModel.createMeeting();

        CreateMeetingViewState secondResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then 2
        assertNull(secondResult.getTopicError());
        assertEquals(EXPECTED_PARTICIPANTS_USER_INPUT_ERROR, secondResult.getParticipantsError());
        assertEquals(EXPECTED_ROOM_USER_INPUT_ERROR, secondResult.getRoomError());

        verifyNoInteractions(repository);
    }

    @Test
    public void given_nothing_is_set_then_everything_is_set_livedata_should_expose_viewmodel_with_no_error() {
        // When
        viewModel.createMeeting();
        viewModel.onTopicChanged("Topic");
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());
        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.CloseActivity);
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());

        verifyRepoAddedMeeting();
    }

    @Test
    public void given_nothing_is_set_then_everything_is_set_livedata_should_expose_viewmodel_with_no_error_bis() {
        // When
        viewModel.createMeeting();
        viewModel.onParticipantsChanged("participant@mail.org");
        viewModel.onTopicChanged("Topic");
        viewModel.onRoomChanged(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());
        ViewAction viewActionResult = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionLiveData());

        // Then
        assertTrue(viewActionResult instanceof ViewAction.CloseActivity);
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertNull(result.getRoomError());

        verifyRepoAddedMeeting();
    }

    // region IN
    @NonNull
    private Clock getDefaultClock(LocalDate localDate, LocalTime localTime) {
        return Clock.fixed(
            LocalDateTime
                .of(
                    localDate,
                    localTime
                )
                .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
        );
    }

    @NonNull
    private LocalTime getDefaultLocalTime() {
        return LocalTime.of(12, 50);
    }

    @NonNull
    private LocalDate getDefaultLocalDate() {
        return LocalDate.of(1991, 2, 26);
    }
    // endregion

    // region OUT
    private void verifyRepoAddedMeeting() {
        verify(repository).addMeeting(
            eq("Topic"),
            eq(LocalTime.of(13, 0)),
            eq(new ArrayList<String>() {{
                add("participant@mail.org");
            }}),
            eq(Room.MARIO)
        );
        verifyNoMoreInteractions(repository);
    }
    // endregion
}
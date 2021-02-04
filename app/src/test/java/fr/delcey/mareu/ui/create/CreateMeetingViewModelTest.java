package fr.delcey.mareu.ui.create;

import android.content.res.Resources;

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
import java.util.ArrayList;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.create.CreateMeetingViewModel.ViewAction;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CreateMeetingViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Resources resources;

    @Mock
    private MeetingRepository repository;

    private CreateMeetingViewModel viewModel;

    @Before
    public void setUp() {
        // Useful for unit testing with time
        Clock clock = Clock.fixed(
            LocalDateTime
                .of(
                    LocalDate.of(1991, 2, 26),
                    LocalTime.of(12, 50)
                )
                .atZone(ZoneOffset.UTC)
                .toInstant(),
            ZoneOffset.UTC
        );

        viewModel = new CreateMeetingViewModel(resources, repository, clock);
    }

    @Test
    public void given_inputs_are_corrects_livedata_should_not_expose_anything_and_view_action_should_expose_close() throws InterruptedException {
        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.createMeeting();

        ViewAction viewActionResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewActionLiveData());
        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals(ViewAction.CLOSE_ACTIVITY, viewActionResult);
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verifyRepoAddedMeeting();
    }

    @Test
    public void given_inputs_are_corrects_with_modified_time_livedata_should_not_expose_anything_and_view_action_should_expose_close() throws InterruptedException {
        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.setTime(21, 30);
        viewModel.createMeeting();

        ViewAction viewActionResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewActionLiveData());
        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals(ViewAction.CLOSE_ACTIVITY, viewActionResult);
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verify(repository).addMeeting(
            eq("Topic"),
            eq(LocalTime.of(21, 30)),
            eq(new ArrayList<String>() {{
                add("participant@mail.org");
            }}),
            eq(Room.MARIO)
        );
    }

    @Test
    public void given_inputs_are_corrects_with_multiple_participants_livedata_should_not_expose_anything_and_view_action_should_expose_close() throws InterruptedException {
        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setParticipants("participant@mail.org,");
        viewModel.setParticipants("participant@mail.org,   participant2@mail.org  ");
        viewModel.setParticipants("participant@mail.org,   participant2@mail.org  ,");
        viewModel.setParticipants("participant@mail.org,   participant2@mail.org  ,     toto@tata.fr, john.smith@consulting.com    ");
        viewModel.setRoom(Room.MARIO);
        viewModel.createMeeting();

        ViewAction viewActionResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewActionLiveData());
        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals(ViewAction.CLOSE_ACTIVITY, viewActionResult);
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verify(repository).addMeeting(
            eq("Topic"),
            eq(LocalTime.of(12, 50)),
            eq(new ArrayList<String>() {{
                add("participant@mail.org");
                add("participant2@mail.org");
                add("toto@tata.fr");
                add("john.smith@consulting.com");
            }}),
            eq(Room.MARIO)
        );
    }

    @Test
    public void given_topic_is_not_set_livedata_should_expose_viewmodel_with_topic_error() throws InterruptedException {
        // Given
        given(resources.getString(R.string.topic_user_input_error)).willReturn("topic_user_input_error");

        // When
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals("topic_user_input_error", result.getTopicError());
        assertNull(result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_topic_is_set_then_deleted_livedata_should_expose_viewmodel_with_topic_error() throws InterruptedException {
        // Given
        given(resources.getString(R.string.topic_user_input_error)).willReturn("topic_user_input_error");

        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.setTopic("");
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals("topic_user_input_error", result.getTopicError());
        assertNull(result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_participants_are_not_set_livedata_should_expose_viewmodel_with_participant_error() throws InterruptedException {
        // Given
        given(resources.getString(R.string.participants_user_input_error)).willReturn("participants_user_input_error");

        // When
        viewModel.setTopic("Topic");
        viewModel.setRoom(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertNull(result.getTopicError());
        assertEquals("participants_user_input_error", result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_participants_are_set_then_deleted_livedata_should_expose_viewmodel_with_participants_error() throws InterruptedException {
        // Given
        given(resources.getString(R.string.participants_user_input_error)).willReturn("participants_user_input_error");

        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.setParticipants("");
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertNull(result.getTopicError());
        assertEquals("participants_user_input_error", result.getParticipantsError());
        assertFalse(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_room_is_not_set_livedata_should_expose_viewmodel_with_room_error() throws InterruptedException {
        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertTrue(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_room_is_set_then_deleted_livedata_should_expose_viewmodel_with_room_error() throws InterruptedException {
        // When
        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.setRoom(Room.UNKNOW);
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertNull(result.getTopicError());
        assertNull(result.getParticipantsError());
        assertTrue(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_nothing_is_set_livedata_should_expose_viewmodel_with_all_errors() throws InterruptedException {
        // Given
        given(resources.getString(R.string.topic_user_input_error)).willReturn("topic_user_input_error");
        given(resources.getString(R.string.participants_user_input_error)).willReturn("participants_user_input_error");

        // When
        viewModel.createMeeting();

        CreateMeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals("topic_user_input_error", result.getTopicError());
        assertEquals("participants_user_input_error", result.getParticipantsError());
        assertTrue(result.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_nothing_is_set_then_topic_is_set_livedata_should_expose_viewmodel_with_some_errors() throws InterruptedException {
        // Given
        given(resources.getString(R.string.topic_user_input_error)).willReturn("topic_user_input_error");
        given(resources.getString(R.string.participants_user_input_error)).willReturn("participants_user_input_error");

        // When
        viewModel.createMeeting();

        CreateMeetingViewState firstResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        viewModel.setTopic("Topic");
        viewModel.createMeeting();

        CreateMeetingViewState secondResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        // Then
        assertEquals("topic_user_input_error", firstResult.getTopicError());
        assertEquals("participants_user_input_error", firstResult.getParticipantsError());
        assertTrue(firstResult.isRoomErrorVisible());

        assertNull(secondResult.getTopicError());
        assertEquals("participants_user_input_error", secondResult.getParticipantsError());
        assertTrue(secondResult.isRoomErrorVisible());
        verifyNoInteractions(repository);
    }

    @Test
    public void given_nothing_is_set_everything_is_set_livedata_should_expose_viewmodel_with_no_error() throws InterruptedException {
        // Given
        given(resources.getString(R.string.topic_user_input_error)).willReturn("topic_user_input_error");
        given(resources.getString(R.string.participants_user_input_error)).willReturn("participants_user_input_error");

        // When
        viewModel.createMeeting();

        CreateMeetingViewState firstResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());

        viewModel.setTopic("Topic");
        viewModel.setParticipants("participant@mail.org");
        viewModel.setRoom(Room.MARIO);
        viewModel.createMeeting();

        CreateMeetingViewState secondResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getCreateMeetingModelLiveData());
        ViewAction viewActionResult = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewActionLiveData());

        // Then
        assertEquals("topic_user_input_error", firstResult.getTopicError());
        assertEquals("participants_user_input_error", firstResult.getParticipantsError());
        assertTrue(firstResult.isRoomErrorVisible());

        assertEquals(ViewAction.CLOSE_ACTIVITY, viewActionResult);
        assertNull(secondResult.getTopicError());
        assertNull(secondResult.getParticipantsError());
        assertFalse(secondResult.isRoomErrorVisible());

        verifyRepoAddedMeeting();
    }

    private void verifyRepoAddedMeeting() {
        verify(repository).addMeeting(
            eq("Topic"),
            eq(LocalTime.of(12, 50)),
            eq(new ArrayList<String>() {{
                add("participant@mail.org");
            }}),
            eq(Room.MARIO)
        );
    }
}
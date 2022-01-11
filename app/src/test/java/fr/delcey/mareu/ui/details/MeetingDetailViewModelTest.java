package fr.delcey.mareu.ui.details;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.MeetingRepository;
import fr.delcey.mareu.data.meeting.model.Meeting;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MeetingDetailViewModelTest {

    private static final String EXPECTED_TOPIC = "EXPECTED_TOPIC";
    private static final String EXPECTED_MEETING_FINISHED = "EXPECTED_MEETING_FINISHED";
    private static final String EXPECTED_MEETING_ONGOING = "EXPECTED_MEETING_ONGOING";
    private static final String EXPECTED_MEETING_IN_2_HOURS = "EXPECTED_MEETING_IN_2_HOURS";
    private static final String EXPECTED_MEETING_IN_10_MINUTES = "EXPECTED_MEETING_IN_10_MINUTES";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application application;

    @Mock
    private Resources resources;

    @Mock
    private MeetingRepository meetingRepository;

    private MutableLiveData<List<Meeting>> meetingsMutableLiveData;

    private MeetingDetailViewModel viewModel;

    @Before
    public void setUp() {
        Clock clock = Clock.fixed(
            LocalDateTime
                .of(
                    LocalDate.of(1991, 2, 26),
                    LocalTime.of(12, 50)
                )
                .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
        );

        meetingsMutableLiveData = new MutableLiveData<>();
        meetingsMutableLiveData.setValue(getDefaultMeetings(getDefaultTime(), getDefaultParticipantsAsString()));
        given(meetingRepository.getMeetingsLiveData()).willReturn(meetingsMutableLiveData);

        given(resources.getString(R.string.meeting_finished)).willReturn(EXPECTED_MEETING_FINISHED);
        given(resources.getString(R.string.meeting_ongoing)).willReturn(EXPECTED_MEETING_ONGOING);
        given(resources.getQuantityString(R.plurals.meeting_in_hours, 2, 2L)).willReturn(EXPECTED_MEETING_IN_2_HOURS);
        given(resources.getQuantityString(R.plurals.meeting_in_minutes, 10, 10L)).willReturn(EXPECTED_MEETING_IN_10_MINUTES);

        viewModel = new MeetingDetailViewModel(application, resources, meetingRepository, clock);
    }

    @Test
    public void given_meeting_is_in_10_minutes() {
        // Given
        viewModel.init(0);

        // When
        MeetingDetailViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(
            getDefaultViewState(getDefaultParticipants(), EXPECTED_MEETING_IN_10_MINUTES),
            result
        );
    }

    @Test
    public void given_meeting_is_in_2_hours() {
        // Given
        meetingsMutableLiveData.setValue(getDefaultMeetings(LocalTime.of(15, 0), getDefaultParticipantsAsString()));
        viewModel.init(0);

        // When
        MeetingDetailViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(
            getDefaultViewState(getDefaultParticipants(), EXPECTED_MEETING_IN_2_HOURS),
            result
        );
    }

    @Test
    public void given_meeting_is_finished() {
        // Given
        meetingsMutableLiveData.setValue(getDefaultMeetings(LocalTime.of(11, 0), getDefaultParticipantsAsString()));
        viewModel.init(0);

        // When
        MeetingDetailViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(
            getDefaultViewState(getDefaultParticipants(), EXPECTED_MEETING_FINISHED),
            result
        );
    }

    @Test
    public void given_meeting_is_ongoing() {
        // Given
        meetingsMutableLiveData.setValue(getDefaultMeetings(LocalTime.of(12, 0), getDefaultParticipantsAsString()));
        viewModel.init(0);

        // When
        MeetingDetailViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(
            getDefaultViewState(getDefaultParticipants(), EXPECTED_MEETING_ONGOING),
            result
        );
    }

    @Test
    public void given_email_is_malformed() {
        // Given
        meetingsMutableLiveData.setValue(getDefaultMeetings(getDefaultTime(), Collections.singletonList("NinoEmailWithoutAtSign")));
        viewModel.init(0);

        // When
        MeetingDetailViewState result = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(
            getDefaultViewState(
                Collections.singletonList(
                    new MeetingDetailViewState.Participant(
                        "NinoEmailWithoutAtSign",
                        "NinoEmailWithoutAtSign"
                    )
                ),
                EXPECTED_MEETING_IN_10_MINUTES
            ),
            result
        );
    }

    @Test
    public void error_case_id_is_missing() {
        // Given
        viewModel.init(666);

        // When
        viewModel.getViewStateLiveData().observeForever(meetingDetailViewState -> {
            throw new AssertionError("This livedata shouldn't expose data with a faulty meetingId");
        });

        // Then
        Mockito.verify(meetingRepository).getMeetingsLiveData();
        Mockito.verifyNoMoreInteractions(meetingRepository);
    }

    @Test
    public void error_case_init_isnt_called() {
        // When
        viewModel.getViewStateLiveData().observeForever(meetingDetailViewState -> {
            throw new AssertionError("This livedata shouldn't expose data when init function isn't called");
        });

        // Then
        Mockito.verifyNoInteractions(meetingRepository);
    }

    @Test
    public void given_participant_is_clicked() {
        // Given
        viewModel.onParticipantClicked(new MeetingDetailViewState.Participant("name", "participantUrl"));

        // When
        MeetingDetailViewModel.MeetingDetailViewAction result = LiveDataTestUtils.getValueForTesting(viewModel.getViewActionSingleLiveEvent());

        // Then
        assertTrue(result instanceof MeetingDetailViewModel.MeetingDetailViewAction.LaunchIntent);
    }

    // region IN
    @NonNull
    private List<Meeting> getDefaultMeetings(@NonNull LocalTime localTime, @NonNull List<String> participants) {
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(new Meeting(0, EXPECTED_TOPIC, localTime, participants, Room.PEACH));
        return meetings;
    }

    @NonNull
    private LocalTime getDefaultTime() {
        return LocalTime.of(13, 0);
    }

    @NonNull
    private List<String> getDefaultParticipantsAsString() {
        List<String> participants = new ArrayList<>();
        participants.add("participant@gmail.com");
        return participants;
    }
    // endregion

    // region OUT
    @NonNull
    private MeetingDetailViewState getDefaultViewState(
        @NonNull List<MeetingDetailViewState.Participant> participants,
        @NonNull String scheduleMessage
    ) {
        return new MeetingDetailViewState(
            R.drawable.ic_room_peach,
            EXPECTED_TOPIC,
            participants,
            scheduleMessage
        );
    }

    @NonNull
    private List<MeetingDetailViewState.Participant> getDefaultParticipants() {
        List<MeetingDetailViewState.Participant> participants = new ArrayList<>();
        participants.add(
            new MeetingDetailViewState.Participant(
                "participant",
                "participant@gmail.com"
            )
        );
        return participants;
    }
    // endregion
}
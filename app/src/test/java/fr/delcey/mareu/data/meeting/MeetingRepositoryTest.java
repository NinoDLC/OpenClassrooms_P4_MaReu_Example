package fr.delcey.mareu.data.meeting;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import fr.delcey.mareu.data.meeting.model.Meeting;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;

public class MeetingRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MeetingRepository meetingRepository;

    @Before
    public void setUp() {
        meetingRepository = new MeetingRepository();
    }

    @Test
    public void shouldAddMeeting() throws InterruptedException {
        // Given
        String meetingTopic = getTopic(0);
        LocalTime time = getTime();
        List<String> meetingParticipants = getParticipants(0);
        Room room = Room.PEACH;

        // When
        meetingRepository.addMeeting(
            meetingTopic,
            time,
            meetingParticipants,
            room
        );
        List<Meeting> results = LiveDataTestUtils.getOrAwaitValue(meetingRepository.getMeetingsLiveData());

        // Then
        assertEquals(1, results.size());
        Meeting result = results.get(0);
        assertEquals(
            result,
            getMeeting(
                0,
                getTime(),
                Room.PEACH
            )
        );
    }

    @Test
    public void shouldAdd2MeetingsAndIncrementId() throws InterruptedException {
        // Given
        String meetingTopic0 = getTopic(0);
        LocalTime time0 = getTime();
        List<String> meetingParticipants0 = getParticipants(0);
        Room room0 = Room.PEACH;

        String meetingTopic1 = getTopic(1);
        LocalTime time1 = LocalTime.of(16, 22);
        List<String> meetingParticipants1 = getParticipants(1);
        Room room1 = Room.MARIO;

        // When
        meetingRepository.addMeeting(
            meetingTopic0,
            time0,
            meetingParticipants0,
            room0
        );
        meetingRepository.addMeeting(
            meetingTopic1,
            time1,
            meetingParticipants1,
            room1
        );
        List<Meeting> results = LiveDataTestUtils.getOrAwaitValue(meetingRepository.getMeetingsLiveData());

        // Then
        assertEquals(2, results.size());
        Meeting result0 = results.get(0);
        assertEquals(
            result0,
            getMeeting(
                0,
                getTime(),
                Room.PEACH
            )
        );
        Meeting result1 = results.get(1);
        assertEquals(
            result1,
            getMeeting(
                1,
                time1,
                room1
            )
        );
    }

    @Test
    public void shouldRemoveMeeting() throws InterruptedException {
        // Given
        String meetingTopic0 = getTopic(0);
        LocalTime time0 = getTime();
        List<String> meetingParticipants0 = getParticipants(0);
        Room room0 = Room.PEACH;

        // When
        meetingRepository.addMeeting(
            meetingTopic0,
            time0,
            meetingParticipants0,
            room0
        );
        meetingRepository.deleteMeeting(0);
        List<Meeting> results = LiveDataTestUtils.getOrAwaitValue(meetingRepository.getMeetingsLiveData());

        // Then
        assertEquals(0, results.size());
    }

    @Test
    public void shouldRemoveMeetingEvenIfListIsEmpty() throws InterruptedException {
        // When
        meetingRepository.deleteMeeting(0);
        List<Meeting> results = LiveDataTestUtils.getOrAwaitValue(meetingRepository.getMeetingsLiveData());

        // Then
        assertEquals(0, results.size());
    }

    @Test
    public void dontRemoveUnexistingMeeting() throws InterruptedException {
        // Given
        String meetingTopic0 = getTopic(0);
        LocalTime time0 = getTime();
        List<String> meetingParticipants0 = getParticipants(0);
        Room room0 = Room.PEACH;

        // When
        meetingRepository.addMeeting(
            meetingTopic0,
            time0,
            meetingParticipants0,
            room0
        );
        meetingRepository.deleteMeeting(666);
        List<Meeting> results = LiveDataTestUtils.getOrAwaitValue(meetingRepository.getMeetingsLiveData());

        // Then
        assertEquals(1, results.size());
    }

    @Test
    public void shouldAddMeetingWithIncrementIdAfterDelete() throws InterruptedException {
        // Given
        String meetingTopic0 = getTopic(0);
        LocalTime time0 = getTime();
        List<String> meetingParticipants0 = getParticipants(0);
        Room room0 = Room.PEACH;

        String meetingTopic1 = getTopic(1);
        LocalTime time1 = LocalTime.of(16, 22);
        List<String> meetingParticipants1 = getParticipants(1);
        Room room1 = Room.MARIO;

        // When
        meetingRepository.addMeeting(
            meetingTopic0,
            time0,
            meetingParticipants0,
            room0
        );
        meetingRepository.deleteMeeting(0);
        meetingRepository.addMeeting(
            meetingTopic1,
            time1,
            meetingParticipants1,
            room1
        );
        List<Meeting> results = LiveDataTestUtils.getOrAwaitValue(meetingRepository.getMeetingsLiveData());

        // Then
        assertEquals(1, results.size());
        Meeting result0 = results.get(0);
        assertEquals(
            result0,
            getMeeting(
                1,
                time1,
                room1
            )
        );
    }

    // region UTILS
    private String getTopic(int index) {
        return "meetingTopic" + index;
    }

    private LocalTime getTime() {
        return LocalTime.of(14, 45);
    }

    private List<String> getParticipants(int index) {
        List<String> meetingParticipants = new ArrayList<>();
        meetingParticipants.add("meetingParticipant" + index);
        return meetingParticipants;
    }


    private Meeting getMeeting(int index, LocalTime time, Room room) {
        return new Meeting(
            index,
            getTopic(index),
            time,
            getParticipants(index),
            room
        );
    }

    // endregion
}
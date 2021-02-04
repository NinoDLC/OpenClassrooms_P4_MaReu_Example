package fr.delcey.mareu.domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import fr.delcey.mareu.BuildConfig;
import fr.delcey.mareu.domain.pojo.Meeting;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.utils.debug.Mock;

public class MeetingRepository {

    private static MeetingRepository instance;

    private MeetingRepository() {
    }

    public static MeetingRepository getInstance() {
        if (instance == null) {
            synchronized (MeetingRepository.class) {
                if (instance == null) {
                    instance = new MeetingRepository();
                }
            }
        }

        return instance;
    }

    private final List<Meeting> meetings = new ArrayList<>();

    private final MutableLiveData<List<Meeting>> meetingsLiveData = new MutableLiveData<>();

    private int highestMeetingId = 0;

    public void addMeeting(
        @NonNull String topic,
        @NonNull LocalTime time,
        @NonNull List<String> participants,
        @NonNull Room room
    ) {
        meetings.add(
            new Meeting(
                highestMeetingId,
                topic,
                time,
                participants,
                room
            )
        );

        highestMeetingId++;

        meetingsLiveData.setValue(meetings);
    }

    public void deleteMeeting(int meetingId) {
        for (Iterator<Meeting> iterator = meetings.iterator(); iterator.hasNext(); ) {
            Meeting meeting = iterator.next();

            if (meeting.getId() == meetingId) {
                iterator.remove();
                break;
            }
        }

        meetingsLiveData.setValue(meetings);
    }

    @NonNull
    public LiveData<List<Meeting>> getMeetingsLiveData() {
        return meetingsLiveData;
    }

    // DEBUG
    public void addDebugMeeting() {
        if (BuildConfig.DEBUG) {
            addMeeting(
                Mock.getRandomMeetingTopic(),
                Mock.getRandomMeetingHour(),
                Mock.getRandomMeetingParticipants(),
                Mock.getRandomMeetingRoom()
            );
        }
    }
}

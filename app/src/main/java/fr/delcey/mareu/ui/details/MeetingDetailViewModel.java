package fr.delcey.mareu.ui.details;

import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.domain.pojo.Meeting;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

public class MeetingDetailViewModel extends ViewModel {

    private final MediatorLiveData<MeetingDetailViewState> viewStateMediatorLiveData = new MediatorLiveData<>();

    @NonNull
    private final Application application;

    @NonNull
    private final Resources resources;

    @NonNull
    private final MeetingRepository meetingRepository;

    @NonNull
    private final Clock clock;

    public MeetingDetailViewModel(@NonNull Application application, @NonNull Resources resources, @NonNull MeetingRepository meetingRepository, @NonNull Clock clock) {
        this.application = application;
        this.resources = resources;
        this.meetingRepository = meetingRepository;
        this.clock = clock;
    }

    public void init(int meetingId) {
        viewStateMediatorLiveData.addSource(meetingRepository.getMeetingsLiveData(), meetings -> {
            Meeting found = null;

            for (Meeting meeting : meetings) {
                if (meeting.getId() == meetingId) {
                    found = meeting;
                    break;
                }
            }

            if (found != null) {
                viewStateMediatorLiveData.setValue(map(found));
            }
        });
    }

    @NonNull
    private MeetingDetailViewState map(@NonNull Meeting meeting) {
        List<MeetingDetailViewState.Participant> participants = new ArrayList<>();

        for (String participantUrl : meeting.getParticipants()) {
            String name;

            int indexOfAtSign =  participantUrl.indexOf('@');

            if (indexOfAtSign != -1) {
                name = participantUrl.substring(0,indexOfAtSign);
            } else {
                name = participantUrl;
            }

            participants.add(new MeetingDetailViewState.Participant(name, participantUrl));
        }

        LocalTime now = LocalTime.now(clock);

        String message;

        if (now.isAfter(meeting.getTime().plusHours(1))) {
            message = resources.getString(R.string.meeting_finished);
        } else if (now.isAfter(meeting.getTime())) {
            message = resources.getString(R.string.meeting_ongoing);
        } else {
            long deltaHour = HOURS.between(now, meeting.getTime());

            if (deltaHour >= 1) {
                message = resources.getQuantityString(R.plurals.meeting_in_hours, (int) deltaHour, deltaHour);
            } else {
                long deltaMinutes = MINUTES.between(now, meeting.getTime());

                message = resources.getQuantityString(R.plurals.meeting_in_minutes, (int) deltaMinutes, deltaMinutes);
            }
        }

        return new MeetingDetailViewState(
            meeting.getRoom().getDrawableResIcon(),
            meeting.getTopic(),
            participants,
            message
        );
    }

    public LiveData<MeetingDetailViewState> getViewStateLiveData() {
        return viewStateMediatorLiveData;
    }

    public void onParticipantClicked(MeetingDetailViewState.Participant participant) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{participant.getParticipantUrl()});
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(application.getPackageManager()) != null) {
            application.startActivity(intent);
        }
    }
}

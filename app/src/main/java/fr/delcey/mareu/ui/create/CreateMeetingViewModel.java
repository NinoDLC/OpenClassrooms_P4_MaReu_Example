package fr.delcey.mareu.ui.create;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.Clock;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.MeetingRepository;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.utils.livedata.SingleLiveEvent;

public class CreateMeetingViewModel extends ViewModel {

    private static final int NEAREST_MINUTE_ROUNDING = 30;

    @NonNull
    private final Resources resources;

    @NonNull
    private final MeetingRepository meetingRepository;

    @NonNull
    private final DateTimeFormatter hourDateTimeFormatter;

    private final MutableLiveData<CreateMeetingViewState> createMeetingViewStateMutableLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<ViewAction> viewActionSingleLiveEvent = new SingleLiveEvent<>();

    @Nullable
    private String topic;
    @NonNull
    private final List<String> participants = new ArrayList<>();
    @Nullable
    private Room room;
    @NonNull
    private LocalTime time;

    public CreateMeetingViewModel(
        @NonNull Resources resources,
        @NonNull MeetingRepository meetingRepository,
        @NonNull DateTimeFormatter hourDateTimeFormatter,
        @NonNull Clock clock
    ) {
        this.resources = resources;
        this.meetingRepository = meetingRepository;
        this.hourDateTimeFormatter = hourDateTimeFormatter;

        this.time = roundTimeToNext30Min(LocalTime.now(clock));

        createMeetingViewStateMutableLiveData.setValue(
            new CreateMeetingViewState(
                Room.values(),
                formatTime(),
                null,
                null,
                null
            )
        );
    }

    @NonNull
    public LiveData<CreateMeetingViewState> getViewStateLiveData() {
        return createMeetingViewStateMutableLiveData;
    }

    public LiveData<ViewAction> getViewActionLiveData() {
        return viewActionSingleLiveEvent;
    }

    public void onTopicChanged(@NonNull String topic) {
        this.topic = topic;

        CreateMeetingViewState currentViewState = createMeetingViewStateMutableLiveData.getValue();

        if (!topic.isEmpty() && currentViewState != null && currentViewState.getTopicError() != null) {
            createMeetingViewStateMutableLiveData.setValue(
                new CreateMeetingViewState(
                    currentViewState.getRooms(),
                    currentViewState.getTime(),
                    null,
                    currentViewState.getParticipantsError(),
                    currentViewState.getRoomError()
                )
            );
        }
    }

    public void onParticipantsChanged(@NonNull String userInputParticipants) {
        participants.clear();

        String[] participantsList = userInputParticipants.split("[,; \n]");

        for (String participant : participantsList) {
            String participantCleaned = participant.trim();

            if (!participantCleaned.isEmpty()) {
                participants.add(participantCleaned);
            }
        }

        CreateMeetingViewState currentViewState = createMeetingViewStateMutableLiveData.getValue();

        if (!participants.isEmpty() && currentViewState != null && currentViewState.getTopicError() != null) {
            createMeetingViewStateMutableLiveData.setValue(
                new CreateMeetingViewState(
                    currentViewState.getRooms(),
                    currentViewState.getTime(),
                    currentViewState.getTopicError(),
                    null,
                    currentViewState.getRoomError()
                )
            );
        }
    }

    public void onRoomChanged(@Nullable Room room) {
        this.room = room;

        CreateMeetingViewState currentViewState = createMeetingViewStateMutableLiveData.getValue();

        if (room != null && currentViewState != null && currentViewState.getRoomError() != null) {
            createMeetingViewStateMutableLiveData.setValue(
                new CreateMeetingViewState(
                    currentViewState.getRooms(),
                    currentViewState.getTime(),
                    currentViewState.getTopicError(),
                    currentViewState.getParticipantsError(),
                    null
                )
            );
        }
    }

    public void onTimeEditTextClicked() {
        viewActionSingleLiveEvent.setValue(new ViewAction.DisplayTimePicker(time.getHour(), time.getMinute()));
    }

    public void onTimeChanged(int hour, int minute) {
        time = LocalTime.of(hour, minute);

        CreateMeetingViewState currentViewState = createMeetingViewStateMutableLiveData.getValue();

        if (currentViewState != null) {
            createMeetingViewStateMutableLiveData.setValue(
                new CreateMeetingViewState(
                    currentViewState.getRooms(),
                    formatTime(),
                    currentViewState.getTopicError(),
                    currentViewState.getParticipantsError(),
                    currentViewState.getRoomError()
                )
            );
        }
    }

    public void createMeeting() {
        VerifiedInputs verifiedInputs = verifyUserInputs();

        if (verifiedInputs != null) {
            meetingRepository.addMeeting(
                verifiedInputs.topic,
                verifiedInputs.time,
                verifiedInputs.participants,
                verifiedInputs.room
            );

            viewActionSingleLiveEvent.setValue(new ViewAction.CloseActivity());
        }
    }

    private VerifiedInputs verifyUserInputs() {
        boolean areUserInputOk = true;

        String topicError;
        if (topic == null || topic.isEmpty()) {
            topicError = resources.getString(R.string.topic_user_input_error);
            areUserInputOk = false;
        } else {
            topicError = null;
        }

        String participantsError;
        if (participants.isEmpty()) {
            participantsError = resources.getString(R.string.participants_user_input_error);
            areUserInputOk = false;
        } else {
            participantsError = null;
        }

        String roomError;
        if (room == null) {
            roomError = resources.getString(R.string.room_user_input_error);
            areUserInputOk = false;
        } else {
            roomError = null;
        }

        createMeetingViewStateMutableLiveData.setValue(
            new CreateMeetingViewState(
                Room.values(),
                formatTime(),
                topicError,
                participantsError,
                roomError
            )
        );

        if (areUserInputOk) {
            return new VerifiedInputs(
                topic,
                participants,
                room,
                time
            );
        }

        return null;
    }

    @NonNull
    private String formatTime() {
        return hourDateTimeFormatter.format(time);
    }

    @NonNull
    private LocalTime roundTimeToNext30Min(@NonNull LocalTime localTime) {
        return localTime
            .truncatedTo(ChronoUnit.HOURS)
            .plusMinutes((long) (NEAREST_MINUTE_ROUNDING * Math.ceil(localTime.getMinute() / (float) NEAREST_MINUTE_ROUNDING)));
    }


    abstract static class ViewAction {
        static class CloseActivity extends ViewAction {
        }

        static class DisplayTimePicker extends ViewAction {
            private final int hour;
            private final int minute;

            DisplayTimePicker(int hour, int minute) {
                this.hour = hour;
                this.minute = minute;
            }

            public int getHour() {
                return hour;
            }

            public int getMinute() {
                return minute;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                DisplayTimePicker that = (DisplayTimePicker) o;
                return hour == that.hour && minute == that.minute;
            }

            @Override
            public int hashCode() {
                return Objects.hash(hour, minute);
            }

            @NonNull
            @Override
            public String toString() {
                return "DisplayTimePicker{" + "hour=" + hour + ", minute=" + minute + '}';
            }
        }
    }

    private static class VerifiedInputs {
        @NonNull
        private final String topic;
        @NonNull
        private final List<String> participants;
        @NonNull
        private final Room room;
        @NonNull
        private final LocalTime time;

        public VerifiedInputs(@NonNull String topic, @NonNull List<String> participants, @NonNull Room room, @NonNull LocalTime time) {
            this.topic = topic;
            this.participants = participants;
            this.room = room;
            this.time = time;
        }
    }
}

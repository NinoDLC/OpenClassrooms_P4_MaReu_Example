package fr.delcey.mareu.ui.create;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.utils.livedata.SingleLiveEvent;

public class CreateMeetingViewModel extends ViewModel {

    @NonNull
    private final Resources resources;

    @NonNull
    private final MeetingRepository meetingRepository;

    private final MutableLiveData<CreateMeetingViewState> createMeetingModelMutableLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<ViewAction> viewActionSingleLiveEvent = new SingleLiveEvent<>();

    @Nullable
    private String topic;
    @NonNull
    private final List<String> participants = new ArrayList<>();
    @NonNull
    private Room room = Room.UNKNOW;
    @NonNull
    private LocalTime time;

    public CreateMeetingViewModel(
        @NonNull Resources resources,
        @NonNull MeetingRepository meetingRepository,
        @NonNull Clock clock
    ) {
        this.resources = resources;
        this.meetingRepository = meetingRepository;
        this.time = LocalTime.now(clock);
    }

    @NonNull
    public LiveData<CreateMeetingViewState> getCreateMeetingModelLiveData() {
        return createMeetingModelMutableLiveData;
    }

    public LiveData<ViewAction> getViewActionLiveData() {
        return viewActionSingleLiveEvent;
    }

    public void setTopic(@NonNull String topic) {
        this.topic = topic;

        CreateMeetingViewState currentModel = createMeetingModelMutableLiveData.getValue();

        if (!topic.isEmpty() && currentModel != null && currentModel.getTopicError() != null) {
            createMeetingModelMutableLiveData.setValue(
                new CreateMeetingViewState(
                    null,
                    currentModel.getParticipantsError(),
                    currentModel.isRoomErrorVisible()
                )
            );
        }
    }

    public void setParticipants(@NonNull String userInputParticipants) {
        participants.clear();

        String[] participantsList = userInputParticipants.split("[,; \n]");

        for (String participant : participantsList) {
            String participantCleaned = participant.trim();

            if (!participantCleaned.isEmpty()) {
                participants.add(participantCleaned);
            }
        }

        CreateMeetingViewState currentModel = createMeetingModelMutableLiveData.getValue();

        if (!participants.isEmpty() && currentModel != null && currentModel.getTopicError() != null) {
            createMeetingModelMutableLiveData.setValue(
                new CreateMeetingViewState(
                    currentModel.getTopicError(),
                    null,
                    currentModel.isRoomErrorVisible()
                )
            );
        }
    }

    public void setRoom(@NonNull Room room) {
        this.room = room;

        CreateMeetingViewState currentModel = createMeetingModelMutableLiveData.getValue();

        if (room != Room.UNKNOW && currentModel != null && currentModel.isRoomErrorVisible()) {
            createMeetingModelMutableLiveData.setValue(
                new CreateMeetingViewState(
                    null,
                    currentModel.getParticipantsError(),
                    currentModel.isRoomErrorVisible()
                )
            );
        }
    }

    public void setTime(int hour, int minute) {
        time = LocalTime.of(hour, minute);
    }

    public void createMeeting() {
        boolean areUserInputOk = verifyUserInputs();

        if (areUserInputOk) {
            assert topic != null;

            meetingRepository.addMeeting(
                topic,
                time,
                participants,
                room
            );

            viewActionSingleLiveEvent.setValue(ViewAction.CLOSE_ACTIVITY);
        }
    }

    private boolean verifyUserInputs() {
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

        boolean isRoomErrorVisible;
        if (room == Room.UNKNOW) {
            isRoomErrorVisible = true;
            areUserInputOk = false;
        } else {
            isRoomErrorVisible = false;
        }

        createMeetingModelMutableLiveData.setValue(
            new CreateMeetingViewState(
                topicError,
                participantsError,
                isRoomErrorVisible
            )
        );

        return areUserInputOk;
    }

    @NonNull
    public CreateMeetingInitModel init() {
        return new CreateMeetingInitModel(Room.values());
    }

    enum ViewAction {
        CLOSE_ACTIVITY
    }
}

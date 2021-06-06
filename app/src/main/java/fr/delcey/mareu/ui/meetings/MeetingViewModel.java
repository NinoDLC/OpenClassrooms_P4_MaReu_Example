package fr.delcey.mareu.ui.meetings;

import android.content.res.Resources;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.MeetingRepository;
import fr.delcey.mareu.data.meeting.model.Meeting;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.data.sorting.SortingParametersRepository;
import fr.delcey.mareu.data.sorting.model.AlphabeticalSortingType;
import fr.delcey.mareu.data.sorting.model.ChronologicalSortingType;
import fr.delcey.mareu.ui.meetings.hour_filter.MeetingViewStateHourFilterItem;
import fr.delcey.mareu.ui.meetings.list.MeetingViewStateItem;
import fr.delcey.mareu.ui.meetings.room_filter.MeetingViewStateRoomFilterItem;
import fr.delcey.mareu.utils.livedata.SingleLiveEvent;

public class MeetingViewModel extends ViewModel {

    private static final int START_OF_HOUR_FILTER = 6;
    private static final int END_OF_HOUR_FILTER = 22;
    private static final int STEP_OF_HOUR_FILTER = 1;

    @NonNull
    private final Resources resources;

    @NonNull
    private final MeetingRepository meetingRepository;

    private final MediatorLiveData<MeetingViewState> meetingViewStateMediatorLiveData = new MediatorLiveData<>();

    // ViewAction : display sorting dialog
    private final SingleLiveEvent<MeetingViewAction> viewActionLiveEvent = new SingleLiveEvent<>();

    // Filter : Room
    private final MutableLiveData<Map<Room, Boolean>> selectedRoomsLiveData = new MutableLiveData<>(populateMapWithAvailableRooms());

    // Filter : Hour
    private final MutableLiveData<Map<LocalTime, Boolean>> selectedHoursLiveData = new MutableLiveData<>(populateMapWithAvailableHours());

    public MeetingViewModel(
        @NonNull final Resources resources,
        @NonNull final MeetingRepository meetingRepository,
        @NonNull final SortingParametersRepository sortingParametersRepository
    ) {
        this.resources = resources;
        this.meetingRepository = meetingRepository;

        wireMeetingModelsMediator(sortingParametersRepository);
    }

    public void onAddDebugMeetingClicked() {
        meetingRepository.addDebugMeeting();
    }

    // region Meetings
    /* **********
     * MEETINGS *
     ********** */

    private void wireMeetingModelsMediator(@NonNull SortingParametersRepository sortingParametersRepository) {
        // Tip : always ask "once" the LiveData from Repository : sometimes they can give you
        // different LiveDatas on subsequent calls, causing weird bugs when "wiring" the Mediator
        final LiveData<List<Meeting>> meetingsLiveData = meetingRepository.getMeetingsLiveData();
        LiveData<AlphabeticalSortingType> alphabeticalSortingTypeLiveData = sortingParametersRepository.getAlphabeticalSortingTypeLiveData();
        LiveData<ChronologicalSortingType> chronologicalSortingTypeLiveData = sortingParametersRepository.getChronologicalSortingTypeLiveData();

        meetingViewStateMediatorLiveData.addSource(meetingsLiveData, meetings ->
            meetingViewStateMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetings,
                    selectedRoomsLiveData.getValue(),
                    selectedHoursLiveData.getValue(),
                    alphabeticalSortingTypeLiveData.getValue(),
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingViewStateMediatorLiveData.addSource(selectedRoomsLiveData, selectedRooms ->
            meetingViewStateMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRooms,
                    selectedHoursLiveData.getValue(),
                    alphabeticalSortingTypeLiveData.getValue(),
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingViewStateMediatorLiveData.addSource(selectedHoursLiveData, selectedHours ->
            meetingViewStateMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRoomsLiveData.getValue(),
                    selectedHours,
                    alphabeticalSortingTypeLiveData.getValue(),
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingViewStateMediatorLiveData.addSource(alphabeticalSortingTypeLiveData, alphabeticalSortingType ->
            meetingViewStateMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRoomsLiveData.getValue(),
                    selectedHoursLiveData.getValue(),
                    alphabeticalSortingType,
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingViewStateMediatorLiveData.addSource(chronologicalSortingTypeLiveData, chronologicalSortingType ->
            meetingViewStateMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRoomsLiveData.getValue(),
                    selectedHoursLiveData.getValue(),
                    alphabeticalSortingTypeLiveData.getValue(),
                    chronologicalSortingType
                )
            )
        );
    }

    @NonNull
    public LiveData<MeetingViewState> getViewStateLiveData() {
        return meetingViewStateMediatorLiveData;
    }

    public SingleLiveEvent<MeetingViewAction> getViewActionSingleLiveEvent() {
        return viewActionLiveEvent;
    }

    public void onDisplaySortingButtonClicked() {
        viewActionLiveEvent.setValue(MeetingViewAction.DISPLAY_SORTING_DIALOG);
    }

    @NonNull
    private MeetingViewState sortAndFilterMeetings(
        @Nullable final List<Meeting> meetings,
        @Nullable final Map<Room, Boolean> selectedRooms,
        @Nullable final Map<LocalTime, Boolean> selectedHours,
        @Nullable final AlphabeticalSortingType alphabeticalSortingType,
        @Nullable final ChronologicalSortingType chronologicalSortingType
    ) {
        if (selectedRooms == null
            || selectedHours == null
            || alphabeticalSortingType == null
            || chronologicalSortingType == null) {
            throw new IllegalStateException("All internal LiveData must be initialized !");
        }

        // Filter meetings...
        List<Meeting> filteredMeetings = getFilteredMeetings(meetings, selectedRooms, selectedHours);

        // ... then sort them...
        Collections.sort(
            filteredMeetings,
            (meeting1, meeting2) -> compareMeetings(meeting1, meeting2, alphabeticalSortingType, chronologicalSortingType)
        );

        // ... and finally, map them !
        List<MeetingViewStateItem> meetingViewStateItems = new ArrayList<>();
        for (Meeting filteredMeeting : filteredMeetings) {
            meetingViewStateItems.add(mapMeeting(filteredMeeting));
        }

        // Compute room filters state
        List<MeetingViewStateRoomFilterItem> meetingViewStateRoomFilterItems = getMeetingViewStateRoomFilterItems(selectedRooms);

        // Compute hour filters state
        List<MeetingViewStateHourFilterItem> meetingViewStateHourFilterItems = getMeetingViewStateHourFilterItems(selectedHours);

        // Expose this to the Activity !
        return new MeetingViewState(
            meetingViewStateItems,
            meetingViewStateRoomFilterItems,
            meetingViewStateHourFilterItems
        );
    }

    @NonNull
    private List<Meeting> getFilteredMeetings(@Nullable List<Meeting> meetings, @NonNull Map<Room, Boolean> selectedRooms, @NonNull Map<LocalTime, Boolean> selectedHours) {
        List<Meeting> filteredMeetings = new ArrayList<>();

        if (meetings == null) {
            return filteredMeetings;
        }

        for (Meeting meeting : meetings) {

            boolean atLeastOneRoomIsSelected = false;
            boolean meetingRoomMatches = false;
            boolean atLeastOneHourIsSelected = false;
            boolean meetingHourMatches = false;

            for (Map.Entry<Room, Boolean> roomEntry : selectedRooms.entrySet()) {
                Room room = roomEntry.getKey();
                boolean isRoomSelected = roomEntry.getValue();

                if (isRoomSelected) {
                    atLeastOneRoomIsSelected = true;
                }

                if (room == meeting.getRoom()) {
                    meetingRoomMatches = isRoomSelected;
                }
            }

            for (Map.Entry<LocalTime, Boolean> hourEntry : selectedHours.entrySet()) {
                LocalTime time = hourEntry.getKey();
                boolean isTimeSelected = hourEntry.getValue();

                if (isTimeSelected) {
                    atLeastOneHourIsSelected = true;
                }

                if (meeting.getTime().equals(time)
                    || (meeting.getTime().isAfter(time) && meeting.getTime().isBefore(time.plusHours(STEP_OF_HOUR_FILTER)))) {
                    meetingHourMatches = isTimeSelected;
                }
            }

            if (!atLeastOneRoomIsSelected) {
                meetingRoomMatches = true;
            }

            if (!atLeastOneHourIsSelected) {
                meetingHourMatches = true;
            }

            if (meetingRoomMatches && meetingHourMatches) {
                filteredMeetings.add(meeting);
            }
        }

        return filteredMeetings;
    }

    private int compareMeetings(
        @NonNull Meeting meeting1,
        @NonNull Meeting meeting2,
        @NonNull AlphabeticalSortingType alphabeticalSortingType,
        @NonNull ChronologicalSortingType chronologicalSortingType
    ) {
        // Filter alphabetically on topic first (because user set an alphabetical sorting)
        if (alphabeticalSortingType.getComparator() != null) {
            int alphabeticalComparison = alphabeticalSortingType.getComparator().compare(meeting1, meeting2);

            // They have a different topic, and date comparison is useless, just return the alphabetical comparison
            if (alphabeticalComparison != 0) {
                return alphabeticalComparison;
            } else {
                // They do have the same topic, let's compare the dates !
                if (chronologicalSortingType.getComparator() != null) {
                    int chronologicalComparison = chronologicalSortingType.getComparator().compare(meeting1, meeting2);

                    // They have different dates, return the chronological delta
                    if (chronologicalComparison != 0) {
                        return chronologicalComparison;
                    }
                }
            }
        } else if (chronologicalSortingType.getComparator() != null) {
            // Filter chronologically on time then (because user set an chronologically sorting)
            int chronologicalComparison = chronologicalSortingType.getComparator().compare(meeting1, meeting2);

            // They have different dates, return the chronological delta
            if (chronologicalComparison != 0) {
                return chronologicalComparison;
            }
        }

        // Default sorting : ids
        return meeting1.getId() - meeting2.getId();
    }

    // This is here we transform the "raw data" (Meeting) into a "user pleasing" view model (MeetingViewStateItem)
    // Meeting is for databases, it has technical values
    // MeetingViewStateItem is for the view (Activity or Fragment), it mostly has Strings or Android Resource Identifiers
    @NonNull
    private MeetingViewStateItem mapMeeting(@NonNull Meeting meeting) {
        return new MeetingViewStateItem(
            meeting.getId(),
            meeting.getRoom().getDrawableResIcon(),
            resources.getString(
                R.string.meeting_title,
                meeting.getTopic(),
                DateTimeFormatter.ofPattern("HH:mm").format(meeting.getTime()),
                resources.getString(meeting.getRoom().getStringResName())
            ),
            stringifyParticipants(meeting.getParticipants())
        );
    }

    @NonNull
    private String stringifyParticipants(@NonNull List<String> participants) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < participants.size(); i++) {
            String participant = participants.get(i);

            result.append(participant);

            if (i + 1 < participants.size()) {
                result.append(", ");
            }
        }

        return result.toString();
    }

    public void onDeleteMeetingClicked(int meetingId) {
        meetingRepository.deleteMeeting(meetingId);
    }
    // endregion

    // region Filter: Room
    public void onRoomSelected(@NonNull Room room) {
        Map<Room, Boolean> selectedRooms = selectedRoomsLiveData.getValue();

        if (selectedRooms == null) {
            return;
        }

        for (Map.Entry<Room, Boolean> entry : selectedRooms.entrySet()) {
            if (entry.getKey() == room) {
                entry.setValue(!entry.getValue());
                break;
            }
        }

        selectedRoomsLiveData.setValue(selectedRooms);
    }

    @NonNull
    private Map<Room, Boolean> populateMapWithAvailableRooms() {
        Map<Room, Boolean> rooms = new LinkedHashMap<>();

        for (Room room : Room.values()) {
            rooms.put(room, false);
        }

        return rooms;
    }

    @NonNull
    private List<MeetingViewStateRoomFilterItem> getMeetingViewStateRoomFilterItems(@NonNull Map<Room, Boolean> selectedRooms) {
        List<MeetingViewStateRoomFilterItem> meetingViewStateRoomFilterItems = new ArrayList<>();
        for (Map.Entry<Room, Boolean> entry : selectedRooms.entrySet()) {
            Room room = entry.getKey();
            Boolean isRoomSelected = entry.getValue();

            @ColorInt int textColorInt = ResourcesCompat.getColor(
                resources,
                isRoomSelected ? android.R.color.white : R.color.chipTextColor,
                null
            );

            meetingViewStateRoomFilterItems.add(
                new MeetingViewStateRoomFilterItem(
                    room,
                    textColorInt,
                    isRoomSelected
                )
            );
        }
        return meetingViewStateRoomFilterItems;
    }
    // endregion

    // region Filter: Hour
    public void onHourSelected(@NonNull LocalTime hour) {
        Map<LocalTime, Boolean> selectedHours = selectedHoursLiveData.getValue();

        if (selectedHours == null) {
            return;
        }

        for (Map.Entry<LocalTime, Boolean> entry : selectedHours.entrySet()) {
            if (entry.getKey().equals(hour)) {
                entry.setValue(!entry.getValue());
                break;
            }
        }

        selectedHoursLiveData.setValue(selectedHours);
    }

    @NonNull
    private Map<LocalTime, Boolean> populateMapWithAvailableHours() {
        Map<LocalTime, Boolean> hours = new LinkedHashMap<>();

        for (int hour = START_OF_HOUR_FILTER; hour <= END_OF_HOUR_FILTER; hour += STEP_OF_HOUR_FILTER) {
            hours.put(LocalTime.of(hour, 0), false);
        }

        return hours;
    }

    @NonNull
    private List<MeetingViewStateHourFilterItem> getMeetingViewStateHourFilterItems(@NonNull Map<LocalTime, Boolean> selectedHours) {
        List<MeetingViewStateHourFilterItem> meetingViewStateHourFilterItems = new ArrayList<>();

        boolean previousHourSelected = false;

        for (Map.Entry<LocalTime, Boolean> entry : selectedHours.entrySet()) {
            LocalTime localTime = entry.getKey();
            Boolean isHourSelected = entry.getValue();
            boolean isNextHourSelected = isHourSelected(selectedHours, localTime.plusHours(STEP_OF_HOUR_FILTER));

            @DrawableRes
            int drawableResBackground;

            @ColorRes
            int textColorRes;

            if (isHourSelected) {
                if (previousHourSelected) {
                    textColorRes = android.R.color.white;
                    if (isNextHourSelected) {
                        drawableResBackground = R.drawable.shape_hour_selection_middle;
                    } else {
                        drawableResBackground = R.drawable.shape_hour_selection_end;
                    }
                } else {
                    if (isNextHourSelected) {
                        textColorRes = android.R.color.white;
                        drawableResBackground = R.drawable.shape_hour_selection_start;
                    } else {
                        textColorRes = android.R.color.black;
                        drawableResBackground = R.drawable.shape_hour_selection_alone;
                    }
                }
            } else {
                drawableResBackground = 0;
                textColorRes = android.R.color.white;
            }

            meetingViewStateHourFilterItems.add(
                new MeetingViewStateHourFilterItem(
                    localTime,
                    localTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    drawableResBackground,
                    textColorRes
                )
            );

            previousHourSelected = isHourSelected;
        }
        return meetingViewStateHourFilterItems;
    }

    private boolean isHourSelected(@NonNull Map<LocalTime, Boolean> selectedHours, @NonNull LocalTime hour) {
        for (Map.Entry<LocalTime, Boolean> entry : selectedHours.entrySet()) {
            if (entry.getKey().equals(hour)) {
                return entry.getValue();
            }
        }

        return false;
    }
    // endregion
}

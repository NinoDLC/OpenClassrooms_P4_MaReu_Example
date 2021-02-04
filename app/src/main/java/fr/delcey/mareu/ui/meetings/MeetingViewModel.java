package fr.delcey.mareu.ui.meetings;

import android.content.res.Resources;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.domain.pojo.Meeting;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.meetings.hour_filter.HourFilterItemModel;
import fr.delcey.mareu.ui.meetings.hour_filter.HourFilterModel;
import fr.delcey.mareu.ui.meetings.meeting.MeetingModel;
import fr.delcey.mareu.ui.meetings.room_filter.RoomFilterItemModel;
import fr.delcey.mareu.ui.meetings.room_filter.RoomFilterModel;
import fr.delcey.mareu.ui.meetings.sort.AlphabeticSortingType;
import fr.delcey.mareu.ui.meetings.sort.ChronologicalSortingType;
import fr.delcey.mareu.utils.livedata.SingleLiveEvent;

public class MeetingViewModel extends ViewModel {

    private static final int START_OF_HOUR_FILTER = 6;
    private static final int END_OF_HOUR_FILTER = 22;
    private static final int STEP_OF_HOUR_FILTER = 2;

    @NonNull
    private final Resources resources;

    @NonNull
    private final MeetingRepository meetingRepository;

    private final MediatorLiveData<List<MeetingModel>> meetingModelsMediatorLiveData = new MediatorLiveData<>();

    // ViewAction : display sorting dialog
    private final MutableLiveData<ViewAction> viewActionLiveEvent = new SingleLiveEvent<>();

    // Filter : Room
    private final MediatorLiveData<RoomFilterModel> roomFilterModelMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Map<Room, Boolean>> selectedRoomsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRoomFilterVisibleLiveData = new MutableLiveData<>();

    // Filter : Hour
    private final MediatorLiveData<HourFilterModel> hourFilterModelMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Map<LocalTime, Boolean>> selectedHoursLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isHourFilterVisibleLiveData = new MutableLiveData<>();

    // Sorting : Alphabetic
    private final MutableLiveData<AlphabeticSortingType> alphabeticSortingTypeLiveData = new MutableLiveData<>(AlphabeticSortingType.NONE);

    // Sorting : Chronological
    private final MutableLiveData<ChronologicalSortingType> chronologicalSortingTypeLiveData = new MutableLiveData<>(ChronologicalSortingType.NONE);

    public MeetingViewModel(
        @NonNull final Resources resources,
        @NonNull final MeetingRepository meetingRepository
    ) {
        this.resources = resources;
        this.meetingRepository = meetingRepository;

        wireMeetingModelsMediator();
        wireFilterRoomMediator();
        wireFilterHourMediator();
    }

    // region Meetings
    /* **********
     * MEETINGS *
     ********** */

    private void wireMeetingModelsMediator() {
        // Tip : always ask "once" the LiveData from Repository : sometimes they can give you
        // different LiveDatas on subsequent calls, causing weird bugs when "wiring" the Mediator
        final LiveData<List<Meeting>> meetingsLiveData = meetingRepository.getMeetingsLiveData();

        meetingModelsMediatorLiveData.addSource(meetingsLiveData, meetings ->
            meetingModelsMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetings,
                    selectedRoomsLiveData.getValue(),
                    selectedHoursLiveData.getValue(),
                    alphabeticSortingTypeLiveData.getValue(),
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingModelsMediatorLiveData.addSource(selectedRoomsLiveData, selectedRooms ->
            meetingModelsMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRooms,
                    selectedHoursLiveData.getValue(),
                    alphabeticSortingTypeLiveData.getValue(),
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingModelsMediatorLiveData.addSource(selectedHoursLiveData, selectedHours ->
            meetingModelsMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRoomsLiveData.getValue(),
                    selectedHours,
                    alphabeticSortingTypeLiveData.getValue(),
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingModelsMediatorLiveData.addSource(alphabeticSortingTypeLiveData, alphabeticSortingType ->
            meetingModelsMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRoomsLiveData.getValue(),
                    selectedHoursLiveData.getValue(),
                    alphabeticSortingType,
                    chronologicalSortingTypeLiveData.getValue()
                )
            )
        );

        meetingModelsMediatorLiveData.addSource(chronologicalSortingTypeLiveData, chronologicalSortingType ->
            meetingModelsMediatorLiveData.setValue(
                sortAndFilterMeetings(
                    meetingsLiveData.getValue(),
                    selectedRoomsLiveData.getValue(),
                    selectedHoursLiveData.getValue(),
                    alphabeticSortingTypeLiveData.getValue(),
                    chronologicalSortingType
                )
            )
        );
    }

    @NonNull
    public LiveData<List<MeetingModel>> getMeetingModelsLiveData() {
        return meetingModelsMediatorLiveData;
    }

    private List<MeetingModel> sortAndFilterMeetings(
        @Nullable final List<Meeting> meetings,
        @Nullable final Map<Room, Boolean> selectedRooms,
        @Nullable final Map<LocalTime, Boolean> selectedHours,
        @Nullable final AlphabeticSortingType alphabeticSortingType,
        @Nullable final ChronologicalSortingType chronologicalSortingType
    ) {
        List<MeetingModel> result = new ArrayList<>();

        if (meetings == null
            || selectedRooms == null
            || selectedHours == null
            || alphabeticSortingType == null
            || chronologicalSortingType == null) {
            // This should never happen... All LiveData are initialized !
            return result;
        }

        List<Meeting> filteredMeetings = new ArrayList<>();

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

                if (meeting.getTime().isAfter(time)
                    && meeting.getTime().isBefore(time.plusHours(STEP_OF_HOUR_FILTER))) {
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

        Collections.sort(
            filteredMeetings,
            (meeting1, meeting2) -> compareMeetings(meeting1, meeting2, alphabeticSortingType, chronologicalSortingType)
        );

        for (Meeting filteredMeeting : filteredMeetings) {
            result.add(mapMeeting(filteredMeeting));
        }

        return result;
    }

    private int compareMeetings(
        @NonNull Meeting meeting1,
        @NonNull Meeting meeting2,
        @NonNull AlphabeticSortingType alphabeticSortingType,
        @NonNull ChronologicalSortingType chronologicalSortingType
    ) {
        // Filter alphabetically on topic first (because user set an alphabetical sorting)
        if (alphabeticSortingType.getComparator() != null) {
            int alphabeticalComparison = alphabeticSortingType.getComparator().compare(meeting1, meeting2);

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

    // This is here we transform the "raw data" (Meeting) into a "user pleasing" view model (MeetingModel)
    // Meeting is for databases, it has technical values
    // MeetingModel is for the view (Activity or Fragment), it mostly has Strings or Android Resource Identifiers
    @NonNull
    private MeetingModel mapMeeting(@NonNull Meeting meeting) {
        return new MeetingModel(
            meeting.getId(),
            meeting.getRoom().getDrawableResIcon(),
            resources.getString(
                R.string.meeting_title,
                meeting.getTopic(),
                meeting.getTime().toString(),
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

    public void deleteMeeting(int meetingId) {
        meetingRepository.deleteMeeting(meetingId);
    }
    // endregion

    // region Filter: Room
    /* **************
     * Filter: Room *
     ************** */

    private void wireFilterRoomMediator() {
        roomFilterModelMediatorLiveData.addSource(selectedRoomsLiveData, selectedRooms ->
            displayOrHideRoomFilter(selectedRooms, isRoomFilterVisibleLiveData.getValue())
        );

        roomFilterModelMediatorLiveData.addSource(isRoomFilterVisibleLiveData, isRoomFilterVisible ->
            displayOrHideRoomFilter(selectedRoomsLiveData.getValue(), isRoomFilterVisible)
        );

        selectedRoomsLiveData.setValue(populateMapWithAvailableRooms());
    }

    // TODO NINO Need unit test
    @NonNull
    public LiveData<RoomFilterModel> getRoomFilterModelLiveData() {
        return roomFilterModelMediatorLiveData;
    }

    private void displayOrHideRoomFilter(
        @Nullable Map<Room, Boolean> selectedRooms,
        @Nullable Boolean isRoomFilterVisible
    ) {
        List<RoomFilterItemModel> roomFilterItemModels = new ArrayList<>();

        if (selectedRooms != null) {
            for (Map.Entry<Room, Boolean> entry : selectedRooms.entrySet()) {
                Room room = entry.getKey();
                Boolean isRoomSelected = entry.getValue();

                roomFilterItemModels.add(new RoomFilterItemModel(room, isRoomSelected != null && isRoomSelected));
            }
        }

        roomFilterModelMediatorLiveData.setValue(
            new RoomFilterModel(
                roomFilterItemModels,
                isRoomFilterVisible != null && isRoomFilterVisible
            )
        );
    }

    // TODO NINO Need unit test
    public void invertRoomFilterVisibility() {
        Boolean previousValue = isRoomFilterVisibleLiveData.getValue();

        if (previousValue == null) {
            previousValue = false;
        }

        isRoomFilterVisibleLiveData.setValue(!previousValue);
    }

    public void setRoomSelected(@NonNull Room room) {
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
            if (room != Room.UNKNOW) {
                rooms.put(room, false);
            }
        }

        return rooms;
    }

    // endregion

    // region Filter: Hour
    /* **************
     * Filter: Hour *
     ************** */
    private void wireFilterHourMediator() {
        hourFilterModelMediatorLiveData.addSource(selectedHoursLiveData, selectedHours ->
            displayOrHideHourFilter(selectedHours, isHourFilterVisibleLiveData.getValue())
        );

        hourFilterModelMediatorLiveData.addSource(isHourFilterVisibleLiveData, isHourFilterVisible ->
            displayOrHideHourFilter(selectedHoursLiveData.getValue(), isHourFilterVisible)
        );

        selectedHoursLiveData.setValue(populateMapWithAvailableHours());
    }

    // TODO NINO Need unit test
    @NonNull
    public LiveData<HourFilterModel> getHourFilterModelLiveData() {
        return hourFilterModelMediatorLiveData;
    }

    private void displayOrHideHourFilter(
        @Nullable Map<LocalTime, Boolean> selectedHours,
        @Nullable Boolean isHourFilterVisible
    ) {
        List<HourFilterItemModel> hourFilterItemModels = new ArrayList<>();

        if (selectedHours != null) {
            boolean previousHourSelected = false;

            for (Map.Entry<LocalTime, Boolean> entry : selectedHours.entrySet()) {
                LocalTime localTime = entry.getKey();
                Boolean isHourSelected = entry.getValue();
                boolean isNextHourSelected = isHourSelected(selectedHours, localTime.plusHours(2));

                @DrawableRes
                int drawableResBackground;

                @ColorInt
                int textColor = resources.getColor(android.R.color.white);

                if (isHourSelected) {
                    if (previousHourSelected) {
                        if (isNextHourSelected) {
                            textColor = resources.getColor(android.R.color.white);
                            drawableResBackground = R.drawable.shape_hour_selection_middle;
                        } else {
                            textColor = resources.getColor(android.R.color.black);
                            drawableResBackground = R.drawable.shape_hour_selection_end;
                        }
                    } else {
                        if (isNextHourSelected) {
                            textColor = resources.getColor(android.R.color.white);
                            drawableResBackground = R.drawable.shape_hour_selection_start;
                        } else {
                            textColor = resources.getColor(android.R.color.black);
                            drawableResBackground = R.drawable.shape_hour_selection_alone;
                        }
                    }
                } else {
                    drawableResBackground = 0;
                }

                hourFilterItemModels.add(
                    new HourFilterItemModel(
                        localTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        drawableResBackground,
                        textColor
                    )
                );

                previousHourSelected = isHourSelected;
            }
        }

        hourFilterModelMediatorLiveData.setValue(
            new HourFilterModel(
                hourFilterItemModels,
                isHourFilterVisible != null && isHourFilterVisible
            )
        );
    }

    private boolean isHourSelected(Map<LocalTime, Boolean> selectedHours, LocalTime hour) {
        for (Map.Entry<LocalTime, Boolean> entry : selectedHours.entrySet()) {
            if (entry.getKey().equals(hour)) {
                return entry.getValue();
            }
        }

        return false;
    }

    public void setHourSelected(@NonNull String hour) {
        Map<LocalTime, Boolean> selectedHours = selectedHoursLiveData.getValue();

        if (selectedHours == null) {
            return;
        }

        LocalTime hourParsed = LocalTime.parse(hour);

        for (Map.Entry<LocalTime, Boolean> entry : selectedHours.entrySet()) {
            if (entry.getKey().equals(hourParsed)) {
                entry.setValue(!entry.getValue());
                break;
            }
        }

        selectedHoursLiveData.setValue(selectedHours);
    }

    // TODO NINO Need unit test
    public void invertHourFilterVisibility() {
        Boolean previousValue = isHourFilterVisibleLiveData.getValue();

        if (previousValue == null) {
            previousValue = false;
        }

        isHourFilterVisibleLiveData.setValue(!previousValue);
    }

    private Map<LocalTime, Boolean> populateMapWithAvailableHours() {
        Map<LocalTime, Boolean> hours = new LinkedHashMap<>();

        for (int hour = START_OF_HOUR_FILTER; hour <= END_OF_HOUR_FILTER; hour += STEP_OF_HOUR_FILTER) {
            hours.put(LocalTime.of(hour, 0), false);
        }

        return hours;
    }

    // endregion

    // TODO NINO Need unit test
    public LiveData<ViewAction> getViewActionLiveData() {
        return viewActionLiveEvent;
    }

    // TODO NINO Need unit test
    public void displaySortingDialog() {
        assert alphabeticSortingTypeLiveData.getValue() != null;
        assert chronologicalSortingTypeLiveData.getValue() != null;

        viewActionLiveEvent.setValue(
            new ViewAction.DisplaySortingDialogViewAction(
                alphabeticSortingTypeLiveData.getValue(),
                chronologicalSortingTypeLiveData.getValue()
            )
        );
    }

    // TODO NINO Need unit test
    public LiveData<AlphabeticSortingType> getAlphabeticSortingTypeLiveData() {
        return alphabeticSortingTypeLiveData;
    }

    public void changeAlphabeticSorting() {
        AlphabeticSortingType type = alphabeticSortingTypeLiveData.getValue();

        AlphabeticSortingType newType;

        if (type == AlphabeticSortingType.AZ) {
            newType = AlphabeticSortingType.ZA;
        } else if (type == AlphabeticSortingType.ZA) {
            newType = AlphabeticSortingType.NONE;
        } else {
            newType = AlphabeticSortingType.AZ;
        }

        alphabeticSortingTypeLiveData.setValue(newType);
    }

    public LiveData<ChronologicalSortingType> getChronologicalSortingTypeLiveData() {
        return chronologicalSortingTypeLiveData;
    }

    public void changeChronologicalSorting() {
        ChronologicalSortingType type = chronologicalSortingTypeLiveData.getValue();

        ChronologicalSortingType newType;

        if (type == ChronologicalSortingType.OLDEST_FIRST) {
            newType = ChronologicalSortingType.NEWEST_FIRST;
        } else if (type == ChronologicalSortingType.NEWEST_FIRST) {
            newType = ChronologicalSortingType.NONE;
        } else {
            newType = ChronologicalSortingType.OLDEST_FIRST;
        }

        chronologicalSortingTypeLiveData.setValue(newType);
    }
}

package fr.delcey.mareu.ui.meetings;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.delcey.mareu.R;
import fr.delcey.mareu.data.meeting.MeetingRepository;
import fr.delcey.mareu.data.meeting.model.Meeting;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.data.sorting.SortingParametersRepository;
import fr.delcey.mareu.data.sorting.model.AlphabeticalSortingType;
import fr.delcey.mareu.data.sorting.model.ChronologicalSortingType;
import fr.delcey.mareu.ui.meetings.hour_filter.MeetingViewStateHourFilterItem;
import fr.delcey.mareu.ui.meetings.list.MeetingViewStateItem;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class MeetingViewModelTest {

    private static final String EXPECTED_FIRST_MAPPED_TITLE = "EXPECTED_FIRST_MAPPED_TITLE";
    private static final String EXPECTED_SECOND_MAPPED_TITLE = "EXPECTED_SECOND_MAPPED_TITLE";
    private static final String EXPECTED_THIRD_MAPPED_TITLE = "EXPECTED_THIRD_MAPPED_TITLE";
    private static final String EXPECTED_FOURTH_MAPPED_TITLE = "EXPECTED_FOURTH_MAPPED_TITLE";

    private static final String FIRST_TOPIC = "Topic A";
    private static final String SECOND_TOPIC = "Topic B";
    private static final String THIRD_TOPIC = "Topic C";
    private static final String FOURTH_TOPIC = "Topic A"; // <-- !! Same topic as first topic !!

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Resources resources;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private SortingParametersRepository sortingParametersRepository;

    private MutableLiveData<List<Meeting>> meetingsMutableLiveData;
    private MutableLiveData<AlphabeticalSortingType> alphabeticalSortingTypeMutableLiveData;
    private MutableLiveData<ChronologicalSortingType> chronologicalSortingTypeMutableLiveData;

    private MeetingViewModel viewModel;

    @Before
    public void setUp() {
        // Reinitialize Livedatas every test
        meetingsMutableLiveData = new MutableLiveData<>();
        alphabeticalSortingTypeMutableLiveData = new MutableLiveData<>();
        chronologicalSortingTypeMutableLiveData = new MutableLiveData<>();

        // Mock LiveDatas returned from Repositories
        given(meetingRepository.getMeetingsLiveData()).willReturn(meetingsMutableLiveData);
        given(sortingParametersRepository.getAlphabeticalSortingTypeLiveData()).willReturn(alphabeticalSortingTypeMutableLiveData);
        given(sortingParametersRepository.getChronologicalSortingTypeLiveData()).willReturn(chronologicalSortingTypeMutableLiveData);

        // Set default values to LiveDatas
        List<Meeting> meetings = get4Meetings();
        meetingsMutableLiveData.setValue(meetings);

        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.NONE);
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.NONE);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        viewModel = new MeetingViewModel(resources, meetingRepository, sortingParametersRepository);

        verify(meetingRepository).getMeetingsLiveData();
        verify(sortingParametersRepository).getAlphabeticalSortingTypeLiveData();
        verify(sortingParametersRepository).getChronologicalSortingTypeLiveData();
    }

    // Nominal test, just to check if everything is working correctly in nominal state
    @Test
    public void nominal_case_given_repository_has_4_meetings_livedata_should_expose_nominal_state() throws InterruptedException {
        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertNominalState(result);
    }

    @Test
    public void initial_state_no_meetings() throws InterruptedException {
        // Given
        meetingsMutableLiveData.setValue(null);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertTrue(result.getMeetingViewStateItems().isEmpty());
    }

    @Test
    public void initial_state_no_meetings_bis() throws InterruptedException {
        // Given
        meetingsMutableLiveData.setValue(get4Meetings());
        meetingsMutableLiveData.setValue(new ArrayList<>());

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertTrue(result.getMeetingViewStateItems().isEmpty());
    }

    // region FILTERS
    /* ***********
     *   HOURS   *
     *********** */
    @Test
    public void given_1_hour_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(18, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());
        assertSecondMeetingIsInPosition(results, 0);

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                12,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(18, 0),
                    "18:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_1_hour_is_selected_livedata_should_expose_1_meeting_bis() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(14, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());
        assertThirdMeetingIsInPosition(results, 0);

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                8,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(14, 0),
                    "14:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_1_hour_is_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(13, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());
        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_2_hours_are_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(18, 0));
        viewModel.onHourSelected(LocalTime.of(14, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());
        assertSecondMeetingIsInPosition(results, 0);
        assertThirdMeetingIsInPosition(results, 1);

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                8,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(14, 0),
                    "14:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            ),
            new Pair<>(
                12,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(18, 0),
                    "18:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_2_hours_then_only_one_hour_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(18, 0));
        viewModel.onHourSelected(LocalTime.of(14, 0));
        viewModel.onHourSelected(LocalTime.of(14, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());
        assertSecondMeetingIsInPosition(results, 0);

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                12,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(18, 0),
                    "18:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_2_hours_then_no_hour_are_selected_livedata_should_expose_nominal_state() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(18, 0));
        viewModel.onHourSelected(LocalTime.of(14, 0));
        viewModel.onHourSelected(LocalTime.of(14, 0));
        viewModel.onHourSelected(LocalTime.of(18, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertNominalState(result);
    }

    @Test
    public void given_filter_hour_on_0_meeting_then_livedata_should_expose_0_meeting() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(8, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                2,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(8, 0),
                    "08:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filter_hour_on_0_meeting_then_livedata_should_expose_0_meeting_bis() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(8, 0));
        viewModel.onHourSelected(LocalTime.of(9, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                2,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(8, 0),
                    "08:00",
                    R.drawable.shape_hour_selection_start,
                    android.R.color.white
                )
            ),
            new Pair<>(
                3,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(9, 0),
                    "09:00",
                    R.drawable.shape_hour_selection_end,
                    android.R.color.white
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filter_hour_on_0_meeting_then_livedata_should_expose_0_meeting_thrice() throws InterruptedException {
        // Given
        viewModel.onHourSelected(LocalTime.of(8, 0));
        viewModel.onHourSelected(LocalTime.of(9, 0));
        viewModel.onHourSelected(LocalTime.of(10, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(
            hourFilterItems,
            new Pair<>(
                2,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(8, 0),
                    "08:00",
                    R.drawable.shape_hour_selection_start,
                    android.R.color.white
                )
            ),
            new Pair<>(
                3,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(9, 0),
                    "09:00",
                    R.drawable.shape_hour_selection_middle,
                    android.R.color.white
                )
            ),
            new Pair<>(
                4,
                new MeetingViewStateHourFilterItem(
                    LocalTime.of(10, 0),
                    "10:00",
                    R.drawable.shape_hour_selection_end,
                    android.R.color.white
                )
            )
        );

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }


    /* **********
     *   ROOM   *
     ********** */
    @Test
    public void given_1_room_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.MEWTWO);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertThirdMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_1_room_is_selected_livedata_should_expose_1_meeting_bis() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.PEACH);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_1_room_is_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.DK);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_2_rooms_are_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.PEACH);
        viewModel.onRoomSelected(Room.MEWTWO);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertThirdMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_2_rooms_then_only_one_room_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.PEACH);
        viewModel.onRoomSelected(Room.MEWTWO);
        viewModel.onRoomSelected(Room.PEACH);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertThirdMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_2_rooms_then_no_room_are_selected_livedata_should_expose_4_meetings() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.MEWTWO);
        viewModel.onRoomSelected(Room.PEACH);
        viewModel.onRoomSelected(Room.MEWTWO);
        viewModel.onRoomSelected(Room.PEACH);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filter_room_on_0_meeting_then_livedata_should_expose_0_meeting() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.LINK);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filter_room_on_0_meeting_then_livedata_should_expose_0_meeting_bis() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.LINK);
        viewModel.onRoomSelected(Room.LUIGI);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filter_room_on_0_meeting_then_livedata_should_expose_0_meeting_thrice() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.LINK);
        viewModel.onRoomSelected(Room.LUIGI);
        viewModel.onRoomSelected(Room.LINK);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }
    // endregion

    // region SORT
    /* **************
     * ALPHABETICAL *
     ************** */

    @Test
    public void given_sorting_is_alphabetical_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given
        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.AZ);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        assertSecondMeetingIsInPosition(results, 2);
        assertThirdMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_sorting_is_inverse_alphabetical_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given
        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.ZA);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertThirdMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertFirstMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_sorting_is_none_alphabetical_livedata_should_expose_4_meetings() throws InterruptedException {
        // Given
        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.NONE);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertNominalState(result);
    }

    /* ***************
     * CHRONOLOGICAL *
     *************** */
    @Test
    public void given_sorting_is_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.OLDEST_FIRST);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertSecondMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_sorting_is_inverse_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.NEWEST_FIRST);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        assertThirdMeetingIsInPosition(results, 1);
        assertFourthMeetingIsInPosition(results, 2);
        assertFirstMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_sorting_is_none_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.NONE);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertNominalState(result);
    }

    /* ***************
     *     BOTH      *
     *************** */
    @Test
    public void given_sorting_is_alphabetical_and_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given
        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.AZ);
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.OLDEST_FIRST);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        assertSecondMeetingIsInPosition(results, 2);
        assertThirdMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }
    // endregion

    @Test
    public void given_filters_then_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given
        viewModel.onRoomSelected(Room.DK);
        viewModel.onHourSelected(LocalTime.of(18, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filter_and_sorting_then_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given
        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.AZ);
        viewModel.onRoomSelected(Room.DK);

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertFourthMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filters_and_sorting_then_livedata_should_expose_2_meeting() throws InterruptedException {
        // Given
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.OLDEST_FIRST);
        viewModel.onRoomSelected(Room.DK);
        viewModel.onHourSelected(LocalTime.of(18, 0));
        viewModel.onHourSelected(LocalTime.of(13, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertFourthMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void given_filters_and_sorting_inverted_then_livedata_should_expose_2_meeting() throws InterruptedException {
        // Given
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.NEWEST_FIRST);
        viewModel.onRoomSelected(Room.DK);
        viewModel.onHourSelected(LocalTime.of(18, 0));
        viewModel.onHourSelected(LocalTime.of(13, 0));

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void verify_viewmodel_deletes_meeting_repository_should_delete_meeting() {
        // When
        viewModel.onDeleteMeetingClicked(666);

        // Then
        verify(meetingRepository).deleteMeeting(666);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void verify_viewmodel_deletes_some_meetings_repository_should_delete_meetings() {
        // When
        viewModel.onDeleteMeetingClicked(666);
        viewModel.onDeleteMeetingClicked(777);
        viewModel.onDeleteMeetingClicked(123);

        // Then
        verify(meetingRepository).deleteMeeting(666);
        verify(meetingRepository).deleteMeeting(777);
        verify(meetingRepository).deleteMeeting(123);
        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    @Test
    public void verify_viewaction() throws InterruptedException {
        // Given
        viewModel.onDisplaySortingButtonClicked();

        // When
        MeetingViewAction result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewActionSingleLiveEvent());

        // Then
        assertEquals(MeetingViewAction.DISPLAY_SORTING_DIALOG, result);
    }

    // Region mock
    @NonNull
    private List<Meeting> get4Meetings() {
        List<Meeting> meetings = new ArrayList<>();

        List<String> participants = new ArrayList<>();
        participants.add("participant1_1@gmail.com");
        participants.add("participant1_2@outlook.com");
        participants.add("participant1_3@subdomain.domain");
        meetings.add(new Meeting(0, FIRST_TOPIC, LocalTime.of(13, 0), participants, Room.PEACH));

        List<String> participants2 = new ArrayList<>();
        participants2.add("participant2_1@gmail.com");
        participants2.add("participant2_2@outlook.com");
        participants2.add("participant2_3@subdomain.domain");
        meetings.add(new Meeting(1, SECOND_TOPIC, LocalTime.of(18, 30), participants2, Room.DK));

        List<String> participants3 = new ArrayList<>();
        participants3.add("participant3_1@gmail.com");
        participants3.add("participant3_2@outlook.com");
        participants3.add("participant3_3@subdomain.domain");
        meetings.add(new Meeting(2, THIRD_TOPIC, LocalTime.of(14, 30), participants3, Room.MEWTWO));

        List<String> participants4 = new ArrayList<>();
        participants4.add("participant4_1@gmail.com");
        participants4.add("participant4_2@outlook.com");
        participants4.add("participant4_3@subdomain.domain");
        meetings.add(new Meeting(3, FOURTH_TOPIC, LocalTime.of(13, 50), participants4, Room.DK));

        return meetings;
    }

    private void provideResourcesFor4Meetings() {
        given(resources.getString(Room.PEACH.getStringResName())).willReturn("Peach room");
        given(resources.getString(Room.DK.getStringResName())).willReturn("DK room");
        given(resources.getString(Room.MEWTWO.getStringResName())).willReturn("Mewtwo room");
        given(
            resources.getString(
                R.string.meeting_title,
                FIRST_TOPIC,
                "13:00",
                "Peach room"
            )
        ).willReturn(EXPECTED_FIRST_MAPPED_TITLE);
        given(
            resources.getString(
                R.string.meeting_title,
                SECOND_TOPIC,
                "18:30",
                "DK room"
            )
        ).willReturn(EXPECTED_SECOND_MAPPED_TITLE);
        given(
            resources.getString(
                R.string.meeting_title,
                THIRD_TOPIC,
                "14:30",
                "Mewtwo room"
            )
        ).willReturn(EXPECTED_THIRD_MAPPED_TITLE);
        given(
            resources.getString(
                R.string.meeting_title,
                FOURTH_TOPIC,
                "13:50",
                "DK room"
            )
        ).willReturn(EXPECTED_FOURTH_MAPPED_TITLE);
    }
    // endregion

    // region Assert
    private void assertNominalState(@NonNull MeetingViewState result) {
        // Meetings
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, result.getMeetingViewStateItems().size());

        assertFirstMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertEquals(17, hourFilterItems.size());

        verifyNoMoreInteractions(meetingRepository, sortingParametersRepository);
    }

    private void assertFirstMeetingIsInPosition(@NonNull List<MeetingViewStateItem> results, int position) {
        assertEquals(EXPECTED_FIRST_MAPPED_TITLE, results.get(position).getTopic());
        assertEquals(Room.PEACH.getDrawableResIcon(), results.get(position).getMeetingIcon());
        assertEquals(0, results.get(position).getMeetingId());
        assertEquals("participant1_1@gmail.com, participant1_2@outlook.com, participant1_3@subdomain.domain", results.get(position).getParticipants());
    }

    private void assertSecondMeetingIsInPosition(@NonNull List<MeetingViewStateItem> results, int position) {
        assertEquals(EXPECTED_SECOND_MAPPED_TITLE, results.get(position).getTopic());
        assertEquals(Room.DK.getDrawableResIcon(), results.get(position).getMeetingIcon());
        assertEquals(1, results.get(position).getMeetingId());
        assertEquals("participant2_1@gmail.com, participant2_2@outlook.com, participant2_3@subdomain.domain", results.get(position).getParticipants());
    }

    private void assertThirdMeetingIsInPosition(@NonNull List<MeetingViewStateItem> results, int position) {
        assertEquals(EXPECTED_THIRD_MAPPED_TITLE, results.get(position).getTopic());
        assertEquals(Room.MEWTWO.getDrawableResIcon(), results.get(position).getMeetingIcon());
        assertEquals(2, results.get(position).getMeetingId());
        assertEquals("participant3_1@gmail.com, participant3_2@outlook.com, participant3_3@subdomain.domain", results.get(position).getParticipants());
    }

    private void assertFourthMeetingIsInPosition(@NonNull List<MeetingViewStateItem> results, int position) {
        assertEquals(EXPECTED_FOURTH_MAPPED_TITLE, results.get(position).getTopic());
        assertEquals(Room.DK.getDrawableResIcon(), results.get(position).getMeetingIcon());
        assertEquals(3, results.get(position).getMeetingId());
        assertEquals("participant4_1@gmail.com, participant4_2@outlook.com, participant4_3@subdomain.domain", results.get(position).getParticipants());
    }

    // Java is so hard to work with collections...
    @SafeVarargs
    private final void assertHourFilterItems(
        @NonNull List<MeetingViewStateHourFilterItem> hourFilterItems,
        Pair<Integer, MeetingViewStateHourFilterItem>... compareByPositionItems
    ) {
        assertEquals(17, hourFilterItems.size());

        int assertionsDone = 0;

        for (Pair<Integer, MeetingViewStateHourFilterItem> compareByPositionItem : compareByPositionItems) {
            int foundCount = 0;

            for (int i = 0; i < hourFilterItems.size(); i++) {
                MeetingViewStateHourFilterItem hourFilterItem = hourFilterItems.get(i);


                if (i == compareByPositionItem.first
                    && hourFilterItem.equals(compareByPositionItem.second)) {
                    foundCount++;
                }
            }

            if (foundCount == 0) {
                throw new AssertionError(
                    "Couldn't find item "
                        + compareByPositionItem.second
                        + " at position "
                        + compareByPositionItem.first
                        + ". Difference(s) between expected values = "
                        + Arrays.toString(compareByPositionItems)
                        + " and items = "
                        + hourFilterItems
                );
            } else if (foundCount > 1) {
                throw new AssertionError(
                    "Found too many matches for item "
                        + compareByPositionItem.second
                        + " at position "
                        + compareByPositionItem.first
                        + ", foundCount = "
                        + foundCount
                        + ". Difference(s) between expected values = "
                        + Arrays.toString(compareByPositionItems)
                        + " and items = "
                        + hourFilterItems
                );
            } else {
                assertionsDone++;
            }
        }

        if (assertionsDone != compareByPositionItems.length) {
            throw new AssertionError(
                "Difference(s) between expected values = "
                    + Arrays.toString(compareByPositionItems)
                    + " and items = "
                    + hourFilterItems
            );
        }
    }
    // endregion
}
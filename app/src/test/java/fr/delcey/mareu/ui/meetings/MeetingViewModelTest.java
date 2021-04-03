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
import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.domain.pojo.Meeting;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.meetings.hour_filter.MeetingViewStateHourFilterItem;
import fr.delcey.mareu.ui.meetings.list.MeetingViewStateItem;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class MeetingViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Resources resources;

    @Mock
    private MeetingRepository repository;

    private MutableLiveData<List<Meeting>> meetingsLiveData;

    private MeetingViewModel viewModel;

    @Before
    public void setUp() {
        meetingsLiveData = new MutableLiveData<>();

        given(repository.getMeetingsLiveData()).willReturn(meetingsLiveData);

        viewModel = new MeetingViewModel(resources, repository);

        verify(repository).getMeetingsLiveData();
    }

    // Basic test, just to "check" if everything is working correctly in "normal" state
    @Test
    public void given_repository_has_4_meetings_livedata_should_expose_4_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
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

        verifyNoMoreInteractions(repository);
    }

    // region FILTERS
    /* ***********
     *   HOURS   *
     *********** */
    @Test
    public void given_1_hour_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("18:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "18:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_1_hour_is_selected_livedata_should_expose_1_meeting_bis() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("14:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "14:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_1_hour_is_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("13:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());
        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_2_hours_are_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("18:00");
        viewModel.setHourSelected("14:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "14:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            ),
            new Pair<>(
                12,
                new MeetingViewStateHourFilterItem(
                    "18:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_2_hours_then_only_one_hour_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("18:00");
        viewModel.setHourSelected("14:00");
        viewModel.setHourSelected("14:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "18:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_2_hours_then_no_hour_are_selected_livedata_should_expose_4_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("18:00");
        viewModel.setHourSelected("14:00");
        viewModel.setHourSelected("14:00");
        viewModel.setHourSelected("18:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());
        assertFirstMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);

        // Hour
        List<MeetingViewStateHourFilterItem> hourFilterItems = result.getMeetingViewStateHourFilterItems();
        assertHourFilterItems(hourFilterItems);

        verifyNoMoreInteractions(repository);
    }


    @Test
    public void given_filter_hour_on_0_meeting_then_livedata_should_expose_0_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("08:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "08:00",
                    R.drawable.shape_hour_selection_alone,
                    android.R.color.black
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filter_hour_on_0_meeting_then_livedata_should_expose_0_meeting_bis() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("08:00");
        viewModel.setHourSelected("09:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "08:00",
                    R.drawable.shape_hour_selection_start,
                    android.R.color.white
                )
            ),
            new Pair<>(
                3,
                new MeetingViewStateHourFilterItem(
                    "09:00",
                    R.drawable.shape_hour_selection_end,
                    android.R.color.white
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filter_hour_on_0_meeting_then_livedata_should_expose_0_meeting_thrice() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setHourSelected("08:00");
        viewModel.setHourSelected("09:00");
        viewModel.setHourSelected("10:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

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
                    "08:00",
                    R.drawable.shape_hour_selection_start,
                    android.R.color.white
                )
            ),
            new Pair<>(
                3,
                new MeetingViewStateHourFilterItem(
                    "09:00",
                    R.drawable.shape_hour_selection_middle,
                    android.R.color.white
                )
            ),
            new Pair<>(
                4,
                new MeetingViewStateHourFilterItem(
                    "10:00",
                    R.drawable.shape_hour_selection_end,
                    android.R.color.white
                )
            )
        );

        verifyNoMoreInteractions(repository);
    }


    /* **********
     *   ROOM   *
     ********** */
    @Test
    public void given_1_room_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.MEWTWO);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertThirdMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_1_room_is_selected_livedata_should_expose_1_meeting_bis() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.PEACH);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_1_room_is_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.DK);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_2_rooms_are_selected_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.PEACH);
        viewModel.setRoomSelected(Room.MEWTWO);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertThirdMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_2_rooms_then_only_one_room_is_selected_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.PEACH);
        viewModel.setRoomSelected(Room.MEWTWO);
        viewModel.setRoomSelected(Room.PEACH);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertThirdMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_2_rooms_then_no_room_are_selected_livedata_should_expose_4_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.MEWTWO);
        viewModel.setRoomSelected(Room.PEACH);
        viewModel.setRoomSelected(Room.MEWTWO);
        viewModel.setRoomSelected(Room.PEACH);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filter_room_on_0_meeting_then_livedata_should_expose_0_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.LINK);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filter_room_on_0_meeting_then_livedata_should_expose_0_meeting_bis() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.LINK);
        viewModel.setRoomSelected(Room.LUIGI);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filter_room_on_0_meeting_then_livedata_should_expose_0_meeting_thrice() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.LINK);
        viewModel.setRoomSelected(Room.LUIGI);
        viewModel.setRoomSelected(Room.LINK);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(0, results.size());
        verifyNoMoreInteractions(repository);
    }
    // endregion

    // region SORT
    /* **************
     * ALPHABETICAL *
     ************** */

    @Test
    public void given_sorting_is_alphabetical_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeAlphabeticSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        assertSecondMeetingIsInPosition(results, 2);
        assertThirdMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_sorting_is_inverse_alphabetical_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeAlphabeticSorting();
        viewModel.changeAlphabeticSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertThirdMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertFourthMeetingIsInPosition(results, 2);
        assertFirstMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_sorting_is_back_to_none_alphabetical_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeAlphabeticSorting();
        viewModel.changeAlphabeticSorting();
        viewModel.changeAlphabeticSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    /* ***************
     * CHRONOLOGICAL *
     *************** */
    @Test
    public void given_sorting_is_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeChronologicalSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertSecondMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_sorting_is_inverse_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeChronologicalSorting();
        viewModel.changeChronologicalSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        assertThirdMeetingIsInPosition(results, 1);
        assertFourthMeetingIsInPosition(results, 2);
        assertFirstMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_sorting_is_back_to_none_chronological_livedata_should_expose_4_meetings_sorted() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeChronologicalSorting();
        viewModel.changeChronologicalSorting();
        viewModel.changeChronologicalSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(4, results.size());

        assertFirstMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        assertThirdMeetingIsInPosition(results, 2);
        assertFourthMeetingIsInPosition(results, 3);
        verifyNoMoreInteractions(repository);
    }

    // endregion

    @Test
    public void given_filters_then_livedata_should_expose_1_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.DK);
        viewModel.setHourSelected("18:00");
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(1, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filter_and_sorting_then_livedata_should_expose_2_meetings() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.changeAlphabeticSorting();
        viewModel.setRoomSelected(Room.DK);
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertFourthMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filters_and_sorting_then_livedata_should_expose_2_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.DK);
        viewModel.setHourSelected("18:00");
        viewModel.setHourSelected("13:00");
        viewModel.changeChronologicalSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertFourthMeetingIsInPosition(results, 0);
        assertSecondMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void given_filters_and_sorting_inverted_then_livedata_should_expose_2_meeting() throws InterruptedException {
        // Given

        // Mock LiveData returned from Repository
        List<Meeting> meetings = get4Meetings();
        meetingsLiveData.setValue(meetings);

        // Mock Resources (getString)
        provideResourcesFor4Meetings();

        // When
        viewModel.setRoomSelected(Room.DK);
        viewModel.setHourSelected("18:00");
        viewModel.setHourSelected("13:00");
        viewModel.changeChronologicalSorting();
        viewModel.changeChronologicalSorting();
        MeetingViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getMeetingViewStateLiveData());

        // Then
        List<MeetingViewStateItem> results = result.getMeetingViewStateItems();
        assertEquals(2, results.size());

        assertSecondMeetingIsInPosition(results, 0);
        assertFourthMeetingIsInPosition(results, 1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void when_viewmodel_deletes_meeting_repository_should_delete_meeting() {
        // When
        viewModel.deleteMeeting(666);

        // Then
        verify(repository).getMeetingsLiveData();
        verify(repository).deleteMeeting(666);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void when_viewmodel_deletes_some_meetings_repository_should_delete_meetings() {
        // When
        viewModel.deleteMeeting(666);
        viewModel.deleteMeeting(777);
        viewModel.deleteMeeting(123);

        // Then
        verify(repository).getMeetingsLiveData();
        verify(repository).deleteMeeting(666);
        verify(repository).deleteMeeting(777);
        verify(repository).deleteMeeting(123);
        verifyNoMoreInteractions(repository);
    }

    // Region mock
    @NonNull
    private List<Meeting> get4Meetings() {
        List<Meeting> meetings = new ArrayList<>();

        List<String> participants = new ArrayList<>();
        participants.add("participant1_1@gmail.com");
        participants.add("participant1_2@outlook.com");
        participants.add("participant1_3@subdomain.domain");
        meetings.add(new Meeting(0, "First topic", LocalTime.of(13, 10), participants, Room.PEACH));

        List<String> participants2 = new ArrayList<>();
        participants2.add("participant2_1@gmail.com");
        participants2.add("participant2_2@outlook.com");
        participants2.add("participant2_3@subdomain.domain");
        meetings.add(new Meeting(1, "Second topic", LocalTime.of(18, 30), participants2, Room.DK));

        List<String> participants3 = new ArrayList<>();
        participants3.add("participant3_1@gmail.com");
        participants3.add("participant3_2@outlook.com");
        participants3.add("participant3_3@subdomain.domain");
        meetings.add(new Meeting(2, "Third topic", LocalTime.of(14, 30), participants3, Room.MEWTWO));

        List<String> participants4 = new ArrayList<>();
        participants4.add("participant4_1@gmail.com");
        participants4.add("participant4_2@outlook.com");
        participants4.add("participant4_3@subdomain.domain");
        meetings.add(new Meeting(3, "Fourth topic", LocalTime.of(13, 50), participants4, Room.DK));

        return meetings;
    }

    private void provideResourcesFor4Meetings() {
        given(resources.getString(Room.PEACH.getStringResName())).willReturn("Peach room");
        given(resources.getString(Room.DK.getStringResName())).willReturn("DK room");
        given(resources.getString(Room.MEWTWO.getStringResName())).willReturn("Mewtwo room");
        given(
            resources.getString(
                R.string.meeting_title,
                "First topic",
                "13:10",
                "Peach room"
            )
        ).willReturn("First mapped title");
        given(
            resources.getString(
                R.string.meeting_title,
                "Second topic",
                "18:30",
                "DK room"
            )
        ).willReturn("Second mapped title");
        given(
            resources.getString(
                R.string.meeting_title,
                "Third topic",
                "14:30",
                "Mewtwo room"
            )
        ).willReturn("Third mapped title");
        given(
            resources.getString(
                R.string.meeting_title,
                "Fourth topic",
                "13:50",
                "DK room"
            )
        ).willReturn("Fourth mapped title");
    }
    // endregion

    // region Assert
    private void assertFirstMeetingIsInPosition(@NonNull List<MeetingViewStateItem> result, int position) {
        assertEquals(result.get(position).getTopic(), "First mapped title");
        assertEquals(result.get(position).getMeetingIcon(), Room.PEACH.getDrawableResIcon());
        assertEquals(result.get(position).getMeetingId(), 0);
        assertEquals(result.get(position).getParticipants(), "participant1_1@gmail.com, participant1_2@outlook.com, participant1_3@subdomain.domain");
    }

    private void assertSecondMeetingIsInPosition(@NonNull List<MeetingViewStateItem> result, int position) {
        assertEquals(result.get(position).getTopic(), "Second mapped title");
        assertEquals(result.get(position).getMeetingIcon(), Room.DK.getDrawableResIcon());
        assertEquals(result.get(position).getMeetingId(), 1);
        assertEquals(result.get(position).getParticipants(), "participant2_1@gmail.com, participant2_2@outlook.com, participant2_3@subdomain.domain");
    }

    private void assertThirdMeetingIsInPosition(@NonNull List<MeetingViewStateItem> result, int position) {
        assertEquals(result.get(position).getTopic(), "Third mapped title");
        assertEquals(result.get(position).getMeetingIcon(), Room.MEWTWO.getDrawableResIcon());
        assertEquals(result.get(position).getMeetingId(), 2);
        assertEquals(result.get(position).getParticipants(), "participant3_1@gmail.com, participant3_2@outlook.com, participant3_3@subdomain.domain");
    }

    private void assertFourthMeetingIsInPosition(@NonNull List<MeetingViewStateItem> result, int position) {
        assertEquals(result.get(position).getTopic(), "Fourth mapped title");
        assertEquals(result.get(position).getMeetingIcon(), Room.DK.getDrawableResIcon());
        assertEquals(result.get(position).getMeetingId(), 3);
        assertEquals(result.get(position).getParticipants(), "participant4_1@gmail.com, participant4_2@outlook.com, participant4_3@subdomain.domain");
    }

    // Java is so hard to work with considering collections...
    @SafeVarargs
    @SuppressWarnings("ConstantConditions")
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
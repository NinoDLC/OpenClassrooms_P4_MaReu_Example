package fr.delcey.mareu.ui.meetings;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.delcey.mareu.BuildConfig;
import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.create.CreateMeetingActivity;
import fr.delcey.mareu.ui.meetings.hour_filter.HourFilterAdapter;
import fr.delcey.mareu.ui.meetings.hour_filter.OnHourSelectedListener;
import fr.delcey.mareu.ui.meetings.meeting.MeetingAdapter;
import fr.delcey.mareu.ui.meetings.room_filter.OnRoomSelectedListener;
import fr.delcey.mareu.ui.meetings.room_filter.RoomFilterAdapter;
import fr.delcey.mareu.ui.meetings.sort.OnMeetingSortChangedListener;
import fr.delcey.mareu.ui.meetings.sort.SortDialogFragment;

import static fr.delcey.mareu.ui.meetings.ViewAction.DisplaySortingDialogViewAction;

public class MeetingActivity extends AppCompatActivity implements
    MeetingAdapter.Listener,
    OnRoomSelectedListener,
    OnHourSelectedListener,
    OnMeetingSortChangedListener {

    private static final String TAG_DIALOG_SORTING = "SortDialogFragment";

    private CoordinatorLayout rootView;

    private MeetingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meeting_activity);

        rootView = findViewById(R.id.meeting_cl_root_view);

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MeetingViewModel.class);

        initToolbar();
        initMeetingsRecyclerView();
        initRoomFilterRecyclerView();
        initHourFilterRecyclerView();
        initSortingDialog();
        initFab();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.meeting_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initMeetingsRecyclerView() {
        final MeetingAdapter adapter = new MeetingAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.meeting_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel.getMeetingModelsLiveData().observe(this, meetingModels -> adapter.submitList(meetingModels));
    }

    private void initRoomFilterRecyclerView() {
        final RoomFilterAdapter roomAdapter = new RoomFilterAdapter(this);
        final RecyclerView recyclerViewRoom = findViewById(R.id.meeting_rv_rooms);
        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRoom.setAdapter(roomAdapter);

        viewModel.getRoomFilterModelLiveData().observe(this, roomFilterModel -> {
            roomAdapter.submitList(roomFilterModel.getRoomFilterItemModels());

            changeVisibilityWithAnimation(recyclerViewRoom, roomFilterModel.isRoomFilterVisible());
        });
    }

    private void initHourFilterRecyclerView() {
        final HourFilterAdapter hourAdapter = new HourFilterAdapter(this);
        final RecyclerView recyclerViewHours = findViewById(R.id.meeting_rv_hours);
        recyclerViewHours.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHours.setAdapter(hourAdapter);

        viewModel.getHourFilterModelLiveData().observe(this, hourFilterModel -> {
            hourAdapter.submitList(hourFilterModel.getHourFilterItemModels());

            changeVisibilityWithAnimation(recyclerViewHours, hourFilterModel.isHourFilterVisible());
        });
    }

    private void initSortingDialog() {
        viewModel.getViewActionLiveData().observe(this, viewAction -> {
            if (viewAction instanceof DisplaySortingDialogViewAction) {
                DisplaySortingDialogViewAction casted = (DisplaySortingDialogViewAction) viewAction;

                SortDialogFragment.newInstance(
                    casted.getAlphabeticSortingType(),
                    casted.getChronologicalSortingType()
                ).show(getSupportFragmentManager(), TAG_DIALOG_SORTING);
            }
        });

        viewModel.getAlphabeticSortingTypeLiveData().observe(this, alphabeticSortingType -> {
            SortDialogFragment sortDialogFragment = getSortDialogFragment();

            if (sortDialogFragment != null) {
                sortDialogFragment.setAlphabeticSortingType(alphabeticSortingType);
            }
        });

        viewModel.getChronologicalSortingTypeLiveData().observe(this, chronologicalSortingType -> {
            SortDialogFragment sortDialogFragment = getSortDialogFragment();

            if (sortDialogFragment != null) {
                sortDialogFragment.setChronologicalSortingType(chronologicalSortingType);
            }
        });
    }

    private void initFab() {
        FloatingActionButton floatingActionButton = findViewById(R.id.meeting_fab);
        floatingActionButton.setOnClickListener(view -> startActivity(CreateMeetingActivity.navigate(MeetingActivity.this)));

        // This is false in release, so it won't be enabled in prod !
        if (BuildConfig.DEBUG) {
            floatingActionButton.setOnLongClickListener(view -> {

                // This is a bad practice, the view should never speak to the Repositories ! (only with its ViewModel)
                MeetingRepository.getInstance().addDebugMeeting();

                return true;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.meeting_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.meeting_menu_sort) {
            viewModel.displaySortingDialog();
            return true;
        } else if (itemId == R.id.meeting_menu_filter_room) {
            viewModel.invertRoomFilterVisibility();
            return true;
        } else if (itemId == R.id.meeting_menu_filter_hour) {
            viewModel.invertHourFilterVisibility();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMeetingClicked(int meetingId) {
        Toast.makeText(this, "Meeting clicked ! Id = " + meetingId, Toast.LENGTH_SHORT)
            .show();
    }

    @Override
    public void onMeetingDeleteClicked(int meetingId) {
        viewModel.deleteMeeting(meetingId);
    }

    @Override
    public void onRoomSelected(@NonNull Room room) {
        viewModel.setRoomSelected(room);
    }

    @Override
    public void onHourSelected(@NonNull String hour) {
        viewModel.setHourSelected(hour);
    }

    @Override
    public void onAlphabeticSortingClicked() {
        viewModel.changeAlphabeticSorting();
    }

    @Override
    public void onChronologicalSortingClicked() {
        viewModel.changeChronologicalSorting();
    }

    @Nullable
    private SortDialogFragment getSortDialogFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_SORTING);

        SortDialogFragment casted = null;

        try {
            casted = (SortDialogFragment) fragment;
        } catch (ClassCastException e) {
            Log.e(MeetingActivity.class.getSimpleName(), "Fragment " + fragment.getClass().getSimpleName() + " is not a SortDialogFragment !", e);
        }

        return casted;
    }

    // A small utility function that invert visibility of a View with a nice animation !
    private void changeVisibilityWithAnimation(@NonNull View view, boolean isVisible) {
        boolean isViewActuallyVisible = view.getVisibility() == View.VISIBLE;

        if (isVisible && !isViewActuallyVisible) {
            // This is the "magical part" that makes the animation automatic
            TransitionManager.beginDelayedTransition(rootView);

            view.setVisibility(View.VISIBLE);
        } else if (!isVisible && isViewActuallyVisible) {
            // This is the "magical part" that makes the animation automatic
            TransitionManager.beginDelayedTransition(rootView);

            view.setVisibility(View.GONE);
        }
    }
}

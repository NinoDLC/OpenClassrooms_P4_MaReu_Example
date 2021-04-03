package fr.delcey.mareu.ui.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.delcey.mareu.BuildConfig;
import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.domain.pojo.Room;
import fr.delcey.mareu.ui.create.CreateMeetingActivity;
import fr.delcey.mareu.ui.details.MeetingDetailActivity;
import fr.delcey.mareu.ui.meetings.hour_filter.HourFilterAdapter;
import fr.delcey.mareu.ui.meetings.hour_filter.OnHourSelectedListener;
import fr.delcey.mareu.ui.meetings.list.MeetingAdapter;
import fr.delcey.mareu.ui.meetings.list.OnMeetingClickedListener;
import fr.delcey.mareu.ui.meetings.room_filter.OnRoomSelectedListener;
import fr.delcey.mareu.ui.meetings.room_filter.RoomFilterAdapter;
import fr.delcey.mareu.ui.meetings.sort.OnMeetingSortChangedListener;
import fr.delcey.mareu.ui.meetings.sort.SortDialogFragment;

import static fr.delcey.mareu.ui.meetings.MeetingViewAction.DisplaySortingDialogMeetingViewAction;

public class MeetingActivity extends AppCompatActivity implements
    OnMeetingClickedListener,
    OnRoomSelectedListener,
    OnHourSelectedListener,
    OnMeetingSortChangedListener {

    private static final String TAG_DIALOG_SORTING = "SortDialogFragment";

    private CoordinatorLayout rootView;
    private RecyclerView recyclerViewRoom;
    private RecyclerView recyclerViewHour;

    private MeetingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meeting_activity);

        rootView = findViewById(R.id.meeting_cl_root);

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MeetingViewModel.class);

        initToolbar();
        initRecyclerViews();
        initSortingDialog();
        initFab();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.meeting_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerViews() {
        final MeetingAdapter adapter = new MeetingAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.meeting_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final RoomFilterAdapter roomAdapter = new RoomFilterAdapter(this);
        recyclerViewRoom = findViewById(R.id.meeting_rv_rooms);
        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRoom.setAdapter(roomAdapter);

        final HourFilterAdapter hourAdapter = new HourFilterAdapter(this);
        recyclerViewHour = findViewById(R.id.meeting_rv_hours);
        recyclerViewHour.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHour.setAdapter(hourAdapter);

        viewModel.getMeetingViewStateLiveData().observe(this, meetingViewState -> {
            adapter.submitList(meetingViewState.getMeetingViewStateItems());
            roomAdapter.submitList(meetingViewState.getMeetingViewStateRoomFilterItems());
            hourAdapter.submitList(meetingViewState.getMeetingViewStateHourFilterItems());
        });
    }

    private void initSortingDialog() {
        viewModel.getViewActionLiveData().observe(this, meetingViewAction -> {
            if (meetingViewAction instanceof DisplaySortingDialogMeetingViewAction) {
                DisplaySortingDialogMeetingViewAction casted = (DisplaySortingDialogMeetingViewAction) meetingViewAction;

                SortDialogFragment.newInstance(
                    casted.getAlphabeticSortingType(),
                    casted.getChronologicalSortingType()
                ).show(getSupportFragmentManager(), TAG_DIALOG_SORTING);
            }
        });

        // TODO REPO
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

                viewModel.addDebugMeeting();

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
            changeVisibilityWithAnimation(recyclerViewRoom);
            return true;
        } else if (itemId == R.id.meeting_menu_filter_hour) {
            changeVisibilityWithAnimation(recyclerViewHour);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMeetingClicked(@NonNull View imageView, @NonNull View textView, int meetingId) {
        Intent intent = MeetingDetailActivity.navigate(this, meetingId);

        Pair<View, String> imageViewPair = new Pair<>(imageView, imageView.getTransitionName());
        Pair<View, String> textViewPair = new Pair<>(textView, textView.getTransitionName());

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imageViewPair,
            textViewPair
        );

        startActivity(intent, options.toBundle());
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

    // TODO NINO REFACTO
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
    private void changeVisibilityWithAnimation(@NonNull View view) {
        boolean isViewActuallyVisible = view.getVisibility() == View.VISIBLE;

        TransitionManager.endTransitions(rootView);

        // This is the "magical part" that makes the animation automatic
        TransitionManager.beginDelayedTransition(rootView);

        if (isViewActuallyVisible) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}

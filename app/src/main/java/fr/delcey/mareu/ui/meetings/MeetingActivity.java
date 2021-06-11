package fr.delcey.mareu.ui.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalTime;

import fr.delcey.mareu.BuildConfig;
import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.data.meeting.model.Room;
import fr.delcey.mareu.ui.create.CreateMeetingActivity;
import fr.delcey.mareu.ui.details.MeetingDetailActivity;
import fr.delcey.mareu.ui.meetings.hour_filter.HourFilterAdapter;
import fr.delcey.mareu.ui.meetings.hour_filter.OnHourSelectedListener;
import fr.delcey.mareu.ui.meetings.list.MeetingAdapter;
import fr.delcey.mareu.ui.meetings.list.OnMeetingClickedListener;
import fr.delcey.mareu.ui.meetings.room_filter.OnRoomSelectedListener;
import fr.delcey.mareu.ui.meetings.room_filter.RoomFilterAdapter;
import fr.delcey.mareu.ui.meetings.sort.SortDialogFragment;

public class MeetingActivity extends AppCompatActivity implements
    OnMeetingClickedListener,
    OnRoomSelectedListener,
    OnHourSelectedListener {

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
        recyclerViewRoom.setAdapter(roomAdapter);

        final HourFilterAdapter hourAdapter = new HourFilterAdapter(this);
        recyclerViewHour = findViewById(R.id.meeting_rv_hours);
        recyclerViewHour.setAdapter(hourAdapter);

        viewModel.getViewStateLiveData().observe(this, viewState -> {
            adapter.submitList(viewState.getMeetingViewStateItems());
            roomAdapter.submitList(viewState.getMeetingViewStateRoomFilterItems());
            hourAdapter.submitList(viewState.getMeetingViewStateHourFilterItems());
        });
    }

    private void initSortingDialog() {
        viewModel.getViewActionSingleLiveEvent().observe(this, viewAction -> {
            if (viewAction == MeetingViewAction.DISPLAY_SORTING_DIALOG) {
                SortDialogFragment.newInstance().show(getSupportFragmentManager(), null);
            }
        });
    }

    private void initFab() {
        FloatingActionButton floatingActionButton = findViewById(R.id.meeting_fab);
        floatingActionButton.setOnClickListener(view -> startActivity(CreateMeetingActivity.navigate(MeetingActivity.this)));

        // This is false in release, so it won't be enabled in prod !
        if (BuildConfig.DEBUG) {
            floatingActionButton.setOnLongClickListener(view -> {

                viewModel.onAddDebugMeetingClicked();

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
            viewModel.onDisplaySortingButtonClicked();
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
    public void onDeleteMeetingClicked(int meetingId) {
        viewModel.onDeleteMeetingClicked(meetingId);
    }

    @Override
    public void onRoomSelected(@NonNull Room room) {
        viewModel.onRoomSelected(room);
    }

    @Override
    public void onHourSelected(@NonNull LocalTime hour) {
        viewModel.onHourSelected(hour);
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

package fr.delcey.mareu.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.ui.details.MeetingDetailViewModel.MeetingDetailViewAction.LaunchIntent;

import static fr.delcey.mareu.ui.TransitionUtils.getMeetingRoomTransitionName;
import static fr.delcey.mareu.ui.TransitionUtils.getMeetingTopicTransitionName;

public class MeetingDetailActivity extends AppCompatActivity {

    private static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";

    public static Intent navigate(@NonNull Context context, int meetingId) {
        Intent intent = new Intent(context, MeetingDetailActivity.class);
        intent.putExtra(EXTRA_MEETING_ID, meetingId);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meeting_detail_activity);

        initToolbar();

        int meetingId = getIntent().getIntExtra(EXTRA_MEETING_ID, -1);

        MeetingDetailViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MeetingDetailViewModel.class);
        if (savedInstanceState == null) {
            viewModel.init(meetingId);
        }

        ImageView roomImageView = findViewById(R.id.detail_meeting_iv_room);
        TextView topicTextView = findViewById(R.id.detail_meeting_tv_topic);
        ChipGroup participantsChipGroup = findViewById(R.id.detail_meeting_cg_participants);
        TextView scheduleTextView = findViewById(R.id.detail_meeting_tv_schedule_message);

        roomImageView.setTransitionName(getMeetingRoomTransitionName(meetingId));
        topicTextView.setTransitionName(getMeetingTopicTransitionName(meetingId));

        viewModel.getViewStateLiveData().observe(this, viewState -> {
            roomImageView.setImageResource(viewState.getMeetingIcon());
            topicTextView.setText(viewState.getTopic());
            manageChips(viewModel, participantsChipGroup, viewState.getParticipants());
            scheduleTextView.setText(viewState.getScheduleMessage());
        });

        viewModel.getViewActionSingleLiveEvent().observe(this, viewAction -> {
            if (viewAction instanceof LaunchIntent) {
                LaunchIntent casted = (LaunchIntent) viewAction;
                if (casted.getIntent().resolveActivity(getPackageManager()) != null) {
                    startActivity(casted.getIntent());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.meeting_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void manageChips(
        @NonNull MeetingDetailViewModel viewModel,
        @NonNull ChipGroup participantsChipGroup,
        @NonNull List<MeetingDetailViewState.Participant> participants
    ) {
        participantsChipGroup.removeAllViews();

        for (MeetingDetailViewState.Participant participant : participants) {
            Chip chip = new Chip(this);
            chip.setId(View.generateViewId());
            chip.setText(participant.getName());
            chip.setOnClickListener(v -> viewModel.onParticipantClicked(participant));
            chip.setEnsureMinTouchTargetSize(false);

            participantsChipGroup.addView(chip);
        }
    }
}

package fr.delcey.mareu.ui.create;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.data.meeting.model.Room;

import static fr.delcey.mareu.ui.create.CreateMeetingViewModel.ViewAction;

public class CreateMeetingFragment extends Fragment {

    @NonNull
    public static CreateMeetingFragment newInstance() {
        return new CreateMeetingFragment();
    }

    private boolean isRoomAutocompleteViewInitialized;

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.create_meeting_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CreateMeetingViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(CreateMeetingViewModel.class);

        EditText topicEditText = view.findViewById(R.id.create_meeting_et_topic);
        initTopicEditText(viewModel, topicEditText);

        EditText participantsEditText = view.findViewById(R.id.create_meeting_et_participants);
        initParticipantsEditText(viewModel, participantsEditText);

        AutoCompleteTextView autocompleteTextViewRoom = view.findViewById(R.id.create_meeting_actv_room);

        EditText timeEditText = view.findViewById(R.id.create_meeting_et_time);
        timeEditText.setOnClickListener(v -> viewModel.onTimeEditTextClicked());

        FloatingActionButton validateButton = view.findViewById(R.id.create_meeting_fab_validate);
        validateButton.setOnClickListener(validateButtonView -> viewModel.createMeeting());

        viewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
            // One of the few "if"s that could be tolerated in Activity or Fragment :
            // this is because spinner adapter is a terrible class not made for MVVM
            // This is purely for performance purposes, it doesn't modify behavior, it can be removed
            if (!isRoomAutocompleteViewInitialized) {
                isRoomAutocompleteViewInitialized = true;
                initRoomAutocompleteView(viewModel, autocompleteTextViewRoom, viewState.getRooms());
            }
            topicEditText.setError(viewState.getTopicError());
            participantsEditText.setError(viewState.getParticipantsError());
            autocompleteTextViewRoom.setError(viewState.getRoomError());
            timeEditText.setText(viewState.getTime());
        });

        viewModel.getViewActionLiveData().observe(getViewLifecycleOwner(), viewAction -> {
            if (viewAction instanceof ViewAction.CloseActivity) {
                // This is bad practice, a fragment shouldn't close its activity, but for simplicity's sake...
                // https://developer.android.com/training/basics/fragments/communicating
                requireActivity().finish();
            } else if (viewAction instanceof ViewAction.DisplayTimePicker) {
                ViewAction.DisplayTimePicker casted = (ViewAction.DisplayTimePicker) viewAction;

                MaterialTimePicker materialDatePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(casted.getHour())
                    .setMinute(casted.getMinute())
                    .build();
                materialDatePicker.addOnPositiveButtonClickListener(button ->
                    viewModel.onTimeChanged(materialDatePicker.getHour(), materialDatePicker.getMinute())
                );
                materialDatePicker.show(getParentFragmentManager(), null);
            }
        });
    }

    private void initTopicEditText(@NonNull CreateMeetingViewModel viewModel, @NonNull EditText topicEditText) {
        topicEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onTopicChanged(s.toString());
            }
        });
    }

    private void initParticipantsEditText(@NonNull CreateMeetingViewModel viewModel, @NonNull EditText participantsEditText) {
        participantsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onParticipantsChanged(s.toString());
            }
        });
    }

    private void initRoomAutocompleteView(
        @NonNull CreateMeetingViewModel viewModel,
        @NonNull AutoCompleteTextView autoCompleteTextView,
        @NonNull Room[] rooms
    ) {
        final CreateMeetingSpinnerAdapter adapter = new CreateMeetingSpinnerAdapter(requireContext(), rooms);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) ->
            viewModel.onRoomChanged(adapter.getItem(position))
        );
    }
}

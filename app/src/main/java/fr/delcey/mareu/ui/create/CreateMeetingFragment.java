package fr.delcey.mareu.ui.create;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.domain.pojo.Room;

import static fr.delcey.mareu.ui.create.CreateMeetingViewModel.ViewAction;

public class CreateMeetingFragment extends Fragment {

    @NonNull
    public static CreateMeetingFragment newInstance() {
        return new CreateMeetingFragment();
    }

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
        initParticipantsEditText(viewModel,participantsEditText);

        TimePicker timePicker = view.findViewById(R.id.create_meeting_tp);
        initTimePicker(viewModel,timePicker);

        FloatingActionButton validateButton = view.findViewById(R.id.create_meeting_fab_validate);
        initValidateButton(viewModel,validateButton);

        Spinner roomSpinner = view.findViewById(R.id.create_meeting_spi_room);
        TextView roomSpinnerError = view.findViewById(R.id.create_meeting_tv_room_error);
        initRoomSpinner(viewModel,roomSpinner, viewModel.init().getSpinnerData());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewModel.setTime(timePicker.getHour(), timePicker.getMinute());
        } else {
            viewModel.setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        }

        viewModel.getCreateMeetingModelLiveData().observe(getViewLifecycleOwner(), createMeetingViewState -> {
            topicEditText.setError(createMeetingViewState.getTopicError());
            participantsEditText.setError(createMeetingViewState.getParticipantsError());
            roomSpinnerError.setVisibility(createMeetingViewState.isRoomErrorVisible() ? View.VISIBLE : View.GONE);
        });

        viewModel.getViewActionLiveData().observe(getViewLifecycleOwner(), viewAction -> {
            if (viewAction == ViewAction.CLOSE_ACTIVITY) {
                // This is bad practice, a fragment shouldn't close its activity, but for simplicity's sake...
                // https://developer.android.com/training/basics/fragments/communicating
                requireActivity().finish();
            }
        });
    }

    private void initTopicEditText(@NonNull CreateMeetingViewModel viewModel, @NonNull EditText topicEditText) {
        topicEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        topicEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        topicEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setTopic(s.toString());
            }
        });
    }

    private void initParticipantsEditText(@NonNull CreateMeetingViewModel viewModel, @NonNull EditText participantsEditText) {
        participantsEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        participantsEditText.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        participantsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setParticipants(s.toString());
            }
        });
    }

    private void initRoomSpinner(@NonNull CreateMeetingViewModel viewModel, @NonNull Spinner roomSpinner, @NonNull Room[] spinnerData) {
        final CreateMeetingSpinnerAdapter adapter = new CreateMeetingSpinnerAdapter(requireContext(), spinnerData);
        roomSpinner.setAdapter(adapter);
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Room room = adapter.getItem(position);

                if (room != null) {
                    viewModel.setRoom(room);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initTimePicker(@NonNull CreateMeetingViewModel viewModel, @NonNull TimePicker timePicker) {
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> viewModel.setTime(hourOfDay, minute));
    }

    private void initValidateButton(@NonNull CreateMeetingViewModel viewModel, @NonNull FloatingActionButton validateButton) {
        validateButton.setOnClickListener(view -> viewModel.createMeeting());
    }
}

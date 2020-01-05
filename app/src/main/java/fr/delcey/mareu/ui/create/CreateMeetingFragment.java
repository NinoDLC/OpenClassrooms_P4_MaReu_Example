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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;
import fr.delcey.mareu.domain.pojo.Room;

import static fr.delcey.mareu.ui.create.CreateMeetingViewModel.ViewAction;

public class CreateMeetingFragment extends Fragment {

    private CreateMeetingViewModel viewModel;

    private EditText topicEditText;
    private EditText participantsEditText;
    private Spinner roomSpinner;
    private TextView roomSpinnerError;
    private TimePicker timePicker;
    private FloatingActionButton validateButton;

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
        View view = inflater.inflate(R.layout.create_meeting_fragment, container, false);

        topicEditText = view.findViewById(R.id.create_meeting_et_topic);
        participantsEditText = view.findViewById(R.id.create_meeting_et_participants);
        roomSpinner = view.findViewById(R.id.create_meeting_spi_room);
        roomSpinnerError = view.findViewById(R.id.create_meeting_tv_room_error);
        timePicker = view.findViewById(R.id.create_meeting_tp);
        validateButton = view.findViewById(R.id.create_meeting_fab_validate);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(CreateMeetingViewModel.class);

        initTopicEditText();
        initParticipantsEditText();
        initTimePicker();
        initValidateButton();

        initRoomSpinner(viewModel.init().getSpinnerData());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewModel.setTime(timePicker.getHour(), timePicker.getMinute());
        } else {
            viewModel.setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        }

        viewModel.getCreateMeetingModelLiveData().observe(getViewLifecycleOwner(), new Observer<CreateMeetingModel>() {
            @Override
            public void onChanged(CreateMeetingModel createMeetingModel) {
                topicEditText.setError(createMeetingModel.getTopicError());
                participantsEditText.setError(createMeetingModel.getParticipantsError());
                roomSpinnerError.setVisibility(createMeetingModel.isRoomErrorVisible() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getViewActionLiveData().observe(getViewLifecycleOwner(), new Observer<ViewAction>() {
            @Override
            public void onChanged(ViewAction viewAction) {
                if (viewAction == ViewAction.CLOSE_ACTIVITY) {
                    // This is bad practice, a fragment shouldn't close its activity, but for simplicity's sake...
                    // https://developer.android.com/training/basics/fragments/communicating
                    requireActivity().finish();
                }
            }
        });
    }

    private void initTopicEditText() {
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

    private void initParticipantsEditText() {
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

    private void initRoomSpinner(Room[] spinnerData) {
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

    private void initTimePicker() {
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                viewModel.setTime(hourOfDay, minute);
            }
        });
    }

    private void initValidateButton() {
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.createMeeting();
            }
        });
    }
}

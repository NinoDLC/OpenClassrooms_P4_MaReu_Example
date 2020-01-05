package fr.delcey.mareu.ui.meetings.sort;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import fr.delcey.mareu.R;

public class SortDialogFragment extends DialogFragment {

    private static final String KEY_ALPHABETIC_SORTING_TYPE = "KEY_ALPHABETIC_SORTING_TYPE";
    private static final String KEY_CHRONOLOGICAL_SORTING_TYPE = "KEY_CHRONOLOGICAL_SORTING_TYPE";

    private TextView textViewAlphabetical;
    private ImageView imageViewAlphabetical;

    private TextView textViewChronological;
    private ImageView imageViewChronological;

    private OnMeetingSortChangedListener listener;

    public static SortDialogFragment newInstance(
        @NonNull AlphabeticSortingType initialAlphabeticSortingType,
        @NonNull ChronologicalSortingType initialChronologicalSortingType
    ) {
        SortDialogFragment fragment = new SortDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(KEY_ALPHABETIC_SORTING_TYPE, initialAlphabeticSortingType);
        args.putSerializable(KEY_CHRONOLOGICAL_SORTING_TYPE, initialChronologicalSortingType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (OnMeetingSortChangedListener) context;
        } catch (ClassCastException e) {
            Log.e(SortDialogFragment.class.getSimpleName(), "Class " + context.getClass().getSimpleName() + " should implement ui.main.sort.OnMeetingSortChangedListener interface !", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.meeting_sorting_dialog_fragment, container, false);

        ViewGroup layoutAlphabetical = view.findViewById(R.id.meeting_sorting_dialog_ll_alphabetical);
        layoutAlphabetical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAlphabeticSortingClicked();
                }
            }
        });
        imageViewAlphabetical = view.findViewById(R.id.meeting_sorting_dialog_iv_alphabetical);
        textViewAlphabetical = view.findViewById(R.id.meeting_sorting_dialog_tv_alphabetical);


        ViewGroup layoutChronological = view.findViewById(R.id.meeting_sorting_dialog_ll_chronological);
        layoutChronological.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChronologicalSortingClicked();
                }
            }
        });
        imageViewChronological = view.findViewById(R.id.meeting_sorting_dialog_iv_chronological);
        textViewChronological = view.findViewById(R.id.meeting_sorting_dialog_tv_chronological);

        if (getArguments() != null) {
            AlphabeticSortingType initialAlphabeticSortingType = (AlphabeticSortingType) getArguments().getSerializable(KEY_ALPHABETIC_SORTING_TYPE);
            ChronologicalSortingType initialChronologicalSortingType = (ChronologicalSortingType) getArguments().getSerializable(KEY_CHRONOLOGICAL_SORTING_TYPE);

            if (initialAlphabeticSortingType != null) {
                setAlphabeticSortingType(initialAlphabeticSortingType);
            }

            if (initialChronologicalSortingType != null) {
                setChronologicalSortingType(initialChronologicalSortingType);
            }
        }

        return view;
    }

    public void setAlphabeticSortingType(@NonNull AlphabeticSortingType alphabeticSortingType) {
        imageViewAlphabetical.setImageState(alphabeticSortingType.getState(), false);
        textViewAlphabetical.setText(alphabeticSortingType.getMessageStringRes());
    }

    public void setChronologicalSortingType(@NonNull ChronologicalSortingType chronologicalSortingType) {
        imageViewChronological.setImageState(chronologicalSortingType.getState(), false);
        textViewChronological.setText(chronologicalSortingType.getMessageStringRes());
    }
}

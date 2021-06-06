package fr.delcey.mareu.ui.meetings.sort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import fr.delcey.mareu.R;
import fr.delcey.mareu.ViewModelFactory;

public class SortDialogFragment extends DialogFragment {

    public static SortDialogFragment newInstance() {
        return new SortDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SortViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(SortViewModel.class);

        final View view = inflater.inflate(R.layout.meeting_sorting_dialog_fragment, container, false);

        ViewGroup layoutAlphabetical = view.findViewById(R.id.meeting_sorting_dialog_ll_alphabetical);
        layoutAlphabetical.setOnClickListener(v -> viewModel.onAlphabeticalSortingClicked());
        ImageView imageViewAlphabetical = view.findViewById(R.id.meeting_sorting_dialog_iv_alphabetical);
        TextView textViewAlphabetical = view.findViewById(R.id.meeting_sorting_dialog_tv_alphabetical);


        ViewGroup layoutChronological = view.findViewById(R.id.meeting_sorting_dialog_ll_chronological);
        layoutChronological.setOnClickListener(v -> viewModel.onChronologicalSortingClicked());
        ImageView imageViewChronological = view.findViewById(R.id.meeting_sorting_dialog_iv_chronological);
        TextView textViewChronological = view.findViewById(R.id.meeting_sorting_dialog_tv_chronological);

        viewModel.getViewStateLiveData().observe(this, viewState -> {
            imageViewAlphabetical.setImageState(viewState.getAlphabeticalImageState(), false);
            textViewAlphabetical.setText(viewState.getAlphabeticalMessageStringRes());

            imageViewChronological.setImageState(viewState.getChronologicalImageState(), false);
            textViewChronological.setText(viewState.getChronologicalMessageStringRes());
        });

        return view;
    }
}

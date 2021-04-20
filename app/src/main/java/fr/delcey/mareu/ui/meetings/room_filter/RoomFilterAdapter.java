package fr.delcey.mareu.ui.meetings.room_filter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import fr.delcey.mareu.R;

public class RoomFilterAdapter extends ListAdapter<MeetingViewStateRoomFilterItem, RoomFilterAdapter.ViewHolder> {

    @NonNull
    private final OnRoomSelectedListener listener;

    public RoomFilterAdapter(@NonNull OnRoomSelectedListener listener) {
        super(new RoomFilterAdapterDiffCallback());

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_room_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @VisibleForTesting
        public final Chip chip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chip = itemView.findViewById(R.id.meeting_room_item_chip);
        }

        public void bind(
            @NonNull final MeetingViewStateRoomFilterItem roomItemModel,
            @NonNull final OnRoomSelectedListener listener
        ) {
            chip.setChipIcon(ContextCompat.getDrawable(chip.getContext(), roomItemModel.getRoom().getDrawableResIcon()));
            chip.setText(roomItemModel.getRoom().getStringResName());
            chip.setTextColor(roomItemModel.getTextColorInt());
            chip.setRippleColorResource(roomItemModel.getRoom().getColorRes());

            int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{-android.R.attr.state_selected}
            };

            int[] colors = new int[]{
                ContextCompat.getColor(chip.getContext(), roomItemModel.getRoom().getColorRes()),
                0
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);

            chip.setChipBackgroundColor(colorStateList);

            chip.setOnCheckedChangeListener(null);
            chip.setChecked(roomItemModel.isSelected());
            chip.setOnCheckedChangeListener((compoundButton, isChecked) -> listener.onRoomSelected(roomItemModel.getRoom()));
        }
    }

    private static class RoomFilterAdapterDiffCallback extends DiffUtil.ItemCallback<MeetingViewStateRoomFilterItem> {
        @Override
        public boolean areItemsTheSame(@NonNull MeetingViewStateRoomFilterItem oldItem, @NonNull MeetingViewStateRoomFilterItem newItem) {
            return oldItem.getRoom().equals(newItem.getRoom());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MeetingViewStateRoomFilterItem oldItem, @NonNull MeetingViewStateRoomFilterItem newItem) {
            return oldItem.isSelected() == newItem.isSelected();
        }
    }
}

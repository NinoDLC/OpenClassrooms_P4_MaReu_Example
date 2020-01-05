package fr.delcey.mareu.ui.meetings.hour_filter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import fr.delcey.mareu.R;

public class HourFilterAdapter extends ListAdapter<HourFilterItemModel, HourFilterAdapter.ViewHolder> {

    @NonNull
    private final OnHourSelectedListener listener;

    public HourFilterAdapter(@NonNull OnHourSelectedListener listener) {
        super(new HourFilterAdapterDiffCallback());

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_hour_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewHour;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewHour = itemView.findViewById(R.id.meeting_hour_filter_tv_hour);
        }

        public void bind(@NonNull final HourFilterItemModel item, @NonNull final OnHourSelectedListener listener) {
            textViewHour.setText(item.getHour());
            textViewHour.setBackgroundResource(item.getDrawableResBackground());
            textViewHour.setTextColor(item.getTextColor());
            textViewHour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHourSelected(item.getHour());
                }
            });
        }
    }

    private static class HourFilterAdapterDiffCallback extends DiffUtil.ItemCallback<HourFilterItemModel> {
        @Override
        public boolean areItemsTheSame(@NonNull HourFilterItemModel oldItem, @NonNull HourFilterItemModel newItem) {
            return oldItem.getHour().equals(newItem.getHour());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HourFilterItemModel oldItem, @NonNull HourFilterItemModel newItem) {
            return oldItem.getDrawableResBackground() == newItem.getDrawableResBackground()
                && oldItem.getTextColor() == newItem.getTextColor();
        }
    }
}
package fr.delcey.mareu.ui.meetings.meeting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import fr.delcey.mareu.R;

public class MeetingAdapter extends ListAdapter<MeetingModel, MeetingAdapter.ViewHolder> {

    @NonNull
    private final Listener listener;

    public MeetingAdapter(@NonNull Listener listener) {
        super(new MeetingAdapterDiffCallback());

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_itemview, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup layout;
        private final ImageView imageViewRoom;
        private final TextView textViewTitle;
        private final TextView textViewParticipants;
        private final ImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.meeting_item_cl);
            imageViewRoom = itemView.findViewById(R.id.meeting_item_iv_room);
            textViewTitle = itemView.findViewById(R.id.meeting_item_tv_title);
            textViewParticipants = itemView.findViewById(R.id.meeting_item_tv_participants);
            imageViewDelete = itemView.findViewById(R.id.meeting_item_iv_delete);
        }

        public void bind(@NonNull final MeetingModel meetingModel, @NonNull final Listener listener) {
            imageViewRoom.setImageResource(meetingModel.getMeetingIcon());
            textViewTitle.setText(meetingModel.getTitle());
            textViewParticipants.setText(meetingModel.getParticipants());

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMeetingDeleteClicked(meetingModel.getMeetingId());
                }
            });

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMeetingClicked(meetingModel.getMeetingId());
                }
            });
        }
    }

    public interface Listener {

        void onMeetingClicked(int meetingId);

        void onMeetingDeleteClicked(int meetingId);
    }

    private static class MeetingAdapterDiffCallback extends DiffUtil.ItemCallback<MeetingModel> {
        @Override
        public boolean areItemsTheSame(@NonNull MeetingModel oldItem, @NonNull MeetingModel newItem) {
            return oldItem.getMeetingId() == newItem.getMeetingId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MeetingModel oldItem, @NonNull MeetingModel newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                && oldItem.getParticipants().equals(newItem.getParticipants())
                && oldItem.getMeetingIcon() == newItem.getMeetingIcon();
        }
    }
}

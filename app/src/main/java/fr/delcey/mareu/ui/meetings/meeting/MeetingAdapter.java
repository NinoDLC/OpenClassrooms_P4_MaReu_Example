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
import fr.delcey.mareu.domain.pojo.Meeting;

public class MeetingAdapter extends ListAdapter<MeetingViewState, MeetingAdapter.ViewHolder> {

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

        public void bind(@NonNull final MeetingViewState meetingViewState, @NonNull final Listener listener) {
            imageViewRoom.setImageResource(meetingViewState.getMeetingIcon());
            textViewTitle.setText(meetingViewState.getTitle());
            textViewParticipants.setText(meetingViewState.getParticipants());

            imageViewDelete.setOnClickListener(view -> listener.onMeetingDeleteClicked(meetingViewState.getMeetingId()));

            layout.setOnClickListener(view -> listener.onMeetingClicked(meetingViewState.getMeetingId()));
        }
    }

    public interface Listener {

        void onMeetingClicked(int meetingId);

        void onMeetingDeleteClicked(int meetingId);
    }

    private static class MeetingAdapterDiffCallback extends DiffUtil.ItemCallback<MeetingViewState> {
        @Override
        public boolean areItemsTheSame(@NonNull MeetingViewState oldItem, @NonNull MeetingViewState newItem) {
            return oldItem.getMeetingId() == newItem.getMeetingId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MeetingViewState oldItem, @NonNull MeetingViewState newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                && oldItem.getParticipants().equals(newItem.getParticipants())
                && oldItem.getMeetingIcon() == newItem.getMeetingIcon();
        }
    }
}

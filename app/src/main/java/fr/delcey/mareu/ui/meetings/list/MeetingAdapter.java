package fr.delcey.mareu.ui.meetings.list;

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

import static fr.delcey.mareu.ui.TransitionUtils.getMeetingRoomTransitionName;
import static fr.delcey.mareu.ui.TransitionUtils.getMeetingTopicTransitionName;

public class MeetingAdapter extends ListAdapter<MeetingViewStateItem, MeetingAdapter.ViewHolder> {

    @NonNull
    private final OnMeetingClickedListener listener;

    public MeetingAdapter(@NonNull OnMeetingClickedListener listener) {
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
        private final TextView textViewTopic;
        private final TextView textViewParticipants;
        private final ImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.meeting_item_cl);
            imageViewRoom = itemView.findViewById(R.id.meeting_item_iv_room);
            textViewTopic = itemView.findViewById(R.id.meeting_item_tv_topic);
            textViewParticipants = itemView.findViewById(R.id.meeting_item_tv_participants);
            imageViewDelete = itemView.findViewById(R.id.meeting_item_iv_delete);
        }

        public void bind(@NonNull final MeetingViewStateItem meetingViewStateItem, @NonNull final OnMeetingClickedListener listener) {
            imageViewRoom.setImageResource(meetingViewStateItem.getMeetingIcon());
            textViewTopic.setText(meetingViewStateItem.getTopic());
            textViewParticipants.setText(meetingViewStateItem.getParticipants());

            imageViewDelete.setOnClickListener(view ->
                listener.onDeleteMeetingClicked(meetingViewStateItem.getMeetingId())
            );

            layout.setOnClickListener(view ->
                listener.onMeetingClicked(imageViewRoom, textViewTopic, meetingViewStateItem.getMeetingId())
            );

            imageViewRoom.setTransitionName(getMeetingRoomTransitionName(meetingViewStateItem.getMeetingId()));
            textViewTopic.setTransitionName(getMeetingTopicTransitionName(meetingViewStateItem.getMeetingId()));
        }
    }

    private static class MeetingAdapterDiffCallback extends DiffUtil.ItemCallback<MeetingViewStateItem> {
        @Override
        public boolean areItemsTheSame(@NonNull MeetingViewStateItem oldItem, @NonNull MeetingViewStateItem newItem) {
            return oldItem.getMeetingId() == newItem.getMeetingId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MeetingViewStateItem oldItem, @NonNull MeetingViewStateItem newItem) {
            return oldItem.getTopic().equals(newItem.getTopic())
                && oldItem.getParticipants().equals(newItem.getParticipants())
                && oldItem.getMeetingIcon() == newItem.getMeetingIcon();
        }
    }
}

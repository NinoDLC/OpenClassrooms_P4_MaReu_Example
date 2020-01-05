package fr.delcey.mareu.ui.create;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.pojo.Room;

class CreateMeetingSpinnerAdapter extends ArrayAdapter<Room> {
    public CreateMeetingSpinnerAdapter(@NonNull Context context, Room[] resource) {
        super(context, R.layout.create_meeting_spinner_item, resource);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    public View getCustomView(int position, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.create_meeting_spinner_item, parent, false);

        ImageView icon = row.findViewById(R.id.create_meeting_item_iv_icon);
        TextView label = row.findViewById(R.id.create_meeting_item_tv_name);

        Room room = getItem(position);

        assert room != null;

        icon.setBackgroundResource(room.getDrawableResIcon());
        label.setText(room.getStringResName());

        return row;
    }
}

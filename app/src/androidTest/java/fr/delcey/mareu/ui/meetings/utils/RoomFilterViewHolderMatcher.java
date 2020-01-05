package fr.delcey.mareu.ui.meetings.utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;

import fr.delcey.mareu.ui.meetings.room_filter.RoomFilterAdapter;

public class RoomFilterViewHolderMatcher extends BoundedMatcher<RecyclerView.ViewHolder, RoomFilterAdapter.ViewHolder> {

    @StringRes
    private final int titleStringRes;

    private String resolvedTitleString;

    public RoomFilterViewHolderMatcher(@StringRes int titleStringRes) {
        super(RoomFilterAdapter.ViewHolder.class);

        this.titleStringRes = titleStringRes;
    }

    @Override
    protected boolean matchesSafely(@NonNull RoomFilterAdapter.ViewHolder item) {
        Context context = item.itemView.getContext();

        try {
            resolvedTitleString = context.getString(titleStringRes);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();

            return false;
        }

        return resolvedTitleString.equals(item.chip.getText().toString());
    }

    @Override
    public void describeTo(@NonNull Description description) {
        description.appendText("ViewHolder with text = " + resolvedTitleString);
    }
}

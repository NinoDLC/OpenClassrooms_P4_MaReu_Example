package fr.delcey.mareu.ui.utils;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;

public class RecyclerViewItemAssertion implements ViewAssertion {
    @IntRange(from = 0)
    private final int position;
    @IdRes
    private final int viewId;
    @NonNull
    private final Matcher<View> matcher;

    public RecyclerViewItemAssertion(@IntRange(from = 0) int position, @IdRes int viewId, @NonNull Matcher<View> matcher) {
        this.viewId = viewId;
        this.position = position;
        this.matcher = matcher;
    }

    @Override
    public void check(View view, @Nullable NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        if (!(view instanceof RecyclerView)) {
            throw new IllegalStateException("The asserted view is not RecyclerView");
        }

        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(position);

        if (viewHolder == null) {
            throw new IllegalStateException("No ViewHolder found for layout position : " + position);
        }

        View childView = viewHolder.itemView.findViewById(viewId);

        if (childView == null) {
            throw new IllegalStateException("No view found with id : " + recyclerView.getResources().getResourceEntryName(viewId));
        }

        ViewMatchers.assertThat(childView, matcher);
    }
}
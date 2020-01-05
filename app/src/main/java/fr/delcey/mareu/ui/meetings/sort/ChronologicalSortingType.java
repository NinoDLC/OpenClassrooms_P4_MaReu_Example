package fr.delcey.mareu.ui.meetings.sort;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.Comparator;

import fr.delcey.mareu.R;
import fr.delcey.mareu.domain.pojo.Meeting;

public enum ChronologicalSortingType {
    OLDEST_FIRST(
        new int[]{-R.attr.state_not_sorted, R.attr.state_sorted, -R.attr.state_invert_sorted},
        R.string.sorting_chronological_sorted,
        new Comparator<Meeting>() {
            @Override
            public int compare(Meeting o1, Meeting o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        }
    ),
    NEWEST_FIRST(
        new int[]{-R.attr.state_not_sorted, -R.attr.state_sorted, R.attr.state_invert_sorted},
        R.string.sorting_chronological_inverted_sorted,
        new Comparator<Meeting>() {
            @Override
            public int compare(Meeting o1, Meeting o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        }
    ),
    NONE(
        new int[]{R.attr.state_not_sorted, -R.attr.state_sorted, -R.attr.state_invert_sorted},
        R.string.sorting_chronological_none,
        null
    );

    private final int[] state;

    @StringRes
    private final int messageStringRes;

    @Nullable
    private final Comparator<Meeting> comparator;

    ChronologicalSortingType(int[] state, @StringRes int messageStringRes, @Nullable Comparator<Meeting> comparator) {
        this.state = state;
        this.messageStringRes = messageStringRes;
        this.comparator = comparator;
    }

    public int[] getState() {
        return state;
    }

    @StringRes
    public int getMessageStringRes() {
        return messageStringRes;
    }

    @Nullable
    public Comparator<Meeting> getComparator() {
        return comparator;
    }
}

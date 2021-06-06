package fr.delcey.mareu.ui.meetings.sort;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Arrays;
import java.util.Objects;

public class SortViewState {

    private final int[] alphabeticalImageState;

    @StringRes
    private final int alphabeticalMessageStringRes;

    private final int[] chronologicalImageState;

    @StringRes
    private final int chronologicalMessageStringRes;

    public SortViewState(
        int[] alphabeticalImageState,
        @StringRes int alphabeticalMessageStringRes,
        int[] chronologicalImageState,
        @StringRes int chronologicalMessageStringRes
    ) {
        this.alphabeticalImageState = alphabeticalImageState;
        this.alphabeticalMessageStringRes = alphabeticalMessageStringRes;
        this.chronologicalImageState = chronologicalImageState;
        this.chronologicalMessageStringRes = chronologicalMessageStringRes;
    }

    public int[] getAlphabeticalImageState() {
        return alphabeticalImageState;
    }

    @StringRes
    public int getAlphabeticalMessageStringRes() {
        return alphabeticalMessageStringRes;
    }

    public int[] getChronologicalImageState() {
        return chronologicalImageState;
    }

    @StringRes
    public int getChronologicalMessageStringRes() {
        return chronologicalMessageStringRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortViewState that = (SortViewState) o;
        return alphabeticalMessageStringRes == that.alphabeticalMessageStringRes &&
            chronologicalMessageStringRes == that.chronologicalMessageStringRes &&
            Arrays.equals(alphabeticalImageState, that.alphabeticalImageState) &&
            Arrays.equals(chronologicalImageState, that.chronologicalImageState);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(alphabeticalMessageStringRes, chronologicalMessageStringRes);
        result = 31 * result + Arrays.hashCode(alphabeticalImageState);
        result = 31 * result + Arrays.hashCode(chronologicalImageState);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "SortViewState{" +
            "alphabeticalImageState=" + Arrays.toString(alphabeticalImageState) +
            ", alphabeticalMessageStringRes=" + alphabeticalMessageStringRes +
            ", chronologicalImageState=" + Arrays.toString(chronologicalImageState) +
            ", chronologicalMessageStringRes=" + chronologicalMessageStringRes +
            '}';
    }
}

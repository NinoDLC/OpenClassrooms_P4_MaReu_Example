package fr.delcey.mareu.ui.meetings;

import androidx.annotation.NonNull;

import fr.delcey.mareu.ui.meetings.sort.AlphabeticSortingType;
import fr.delcey.mareu.ui.meetings.sort.ChronologicalSortingType;

abstract class MeetingViewAction {

    public static class DisplaySortingDialogMeetingViewAction extends MeetingViewAction {

        @NonNull
        private final AlphabeticSortingType alphabeticSortingType;

        @NonNull
        private final ChronologicalSortingType chronologicalSortingType;

        public DisplaySortingDialogMeetingViewAction(
            @NonNull AlphabeticSortingType alphabeticSortingType,
            @NonNull ChronologicalSortingType chronologicalSortingType
        ) {
            this.alphabeticSortingType = alphabeticSortingType;
            this.chronologicalSortingType = chronologicalSortingType;
        }

        @NonNull
        public AlphabeticSortingType getAlphabeticSortingType() {
            return alphabeticSortingType;
        }

        @NonNull
        public ChronologicalSortingType getChronologicalSortingType() {
            return chronologicalSortingType;
        }
    }
}

package fr.delcey.mareu.ui.meetings;

import androidx.annotation.NonNull;

import fr.delcey.mareu.ui.meetings.sort.AlphabeticSortingType;
import fr.delcey.mareu.ui.meetings.sort.ChronologicalSortingType;

abstract class ViewAction {

    public static class DisplaySortingDialogViewAction extends ViewAction {

        @NonNull
        private final AlphabeticSortingType alphabeticSortingType;

        @NonNull
        private final ChronologicalSortingType chronologicalSortingType;

        public DisplaySortingDialogViewAction(
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

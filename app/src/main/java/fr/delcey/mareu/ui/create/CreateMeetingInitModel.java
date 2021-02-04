package fr.delcey.mareu.ui.create;

import androidx.annotation.NonNull;

import fr.delcey.mareu.domain.pojo.Room;

class CreateMeetingInitModel {

    @NonNull
    private final Room[] spinnerData;

    CreateMeetingInitModel(@NonNull Room[] spinnerData) {
        this.spinnerData = spinnerData;
    }

    @NonNull
    public Room[] getSpinnerData() {
        return spinnerData;
    }
}

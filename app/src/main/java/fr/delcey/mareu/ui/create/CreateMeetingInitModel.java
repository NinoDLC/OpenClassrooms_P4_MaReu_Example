package fr.delcey.mareu.ui.create;

import fr.delcey.mareu.domain.pojo.Room;

class CreateMeetingInitModel {

    private final Room[] spinnerData;

    CreateMeetingInitModel(Room[] spinnerData) {
        this.spinnerData = spinnerData;
    }

    public Room[] getSpinnerData() {
        return spinnerData;
    }
}

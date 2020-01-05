package fr.delcey.mareu.ui.meetings.hour_filter;

import androidx.annotation.NonNull;

import java.util.List;

public class HourFilterModel {

    @NonNull
    private final List<HourFilterItemModel> hourFilterItemModels;

    private final boolean isHourFilterVisible;

    public HourFilterModel(@NonNull List<HourFilterItemModel> hourFilterItemModels, boolean isHourFilterVisible) {
        this.hourFilterItemModels = hourFilterItemModels;
        this.isHourFilterVisible = isHourFilterVisible;
    }

    @NonNull
    public List<HourFilterItemModel> getHourFilterItemModels() {
        return hourFilterItemModels;
    }

    public boolean isHourFilterVisible() {
        return isHourFilterVisible;
    }
}

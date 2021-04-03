package fr.delcey.mareu.ui.meetings.hour_filter;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.Objects;

public class MeetingViewStateHourFilterItem {

    @NonNull
    private final String hour;

    @DrawableRes
    private final int drawableResBackground;

    @ColorRes
    private final int textColorRes;

    public MeetingViewStateHourFilterItem(@NonNull String hour, @DrawableRes int drawableResBackground, @ColorRes int textColorRes) {
        this.hour = hour;
        this.drawableResBackground = drawableResBackground;
        this.textColorRes = textColorRes;
    }

    @NonNull
    public String getHour() {
        return hour;
    }

    @DrawableRes
    public int getDrawableResBackground() {
        return drawableResBackground;
    }

    @ColorRes
    public int getTextColorRes() {
        return textColorRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingViewStateHourFilterItem that = (MeetingViewStateHourFilterItem) o;
        return drawableResBackground == that.drawableResBackground &&
            textColorRes == that.textColorRes &&
            hour.equals(that.hour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, drawableResBackground, textColorRes);
    }

    @NonNull
    @Override
    public String toString() {
        return "MeetingViewStateHourFilterItem{" +
            "hour='" + hour + '\'' +
            ", drawableResBackground=" + drawableResBackground +
            ", textColorRes=" + textColorRes +
            '}';
    }
}

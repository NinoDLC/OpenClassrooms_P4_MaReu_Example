package fr.delcey.mareu.ui.meetings.hour_filter;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class HourFilterItemModel {

    @NonNull
    private final String hour;

    @DrawableRes
    private final int drawableResBackground;

    @ColorInt
    private final int textColor;

    public HourFilterItemModel(@NonNull String hour, @DrawableRes int drawableResBackground, @ColorInt int textColor) {
        this.hour = hour;
        this.drawableResBackground = drawableResBackground;
        this.textColor = textColor;
    }

    @NonNull
    public String getHour() {
        return hour;
    }

    @DrawableRes
    public int getDrawableResBackground() {
        return drawableResBackground;
    }

    @ColorInt
    public int getTextColor() {
        return textColor;
    }
}

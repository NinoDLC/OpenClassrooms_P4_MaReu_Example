package fr.delcey.mareu.domain.pojo;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import fr.delcey.mareu.R;

public enum Room {
    UNKNOW(R.string.empty, R.drawable.ic_room_unknow, android.R.color.white),
    PEACH(R.string.peach, R.drawable.ic_room_peach, R.color.peach),
    MARIO(R.string.mario, R.drawable.ic_room_mario, R.color.mario),
    LUIGI(R.string.luigi, R.drawable.ic_room_luigi, R.color.luigi),
    MEWTWO(R.string.mewtwo, R.drawable.ic_room_mewtwo, R.color.mewtwo),
    WARIO(R.string.wario, R.drawable.ic_room_wario, R.color.wario),
    LINK(R.string.link, R.drawable.ic_room_link, R.color.link),
    YOSHI(R.string.yoshi, R.drawable.ic_room_yoshi, R.color.yoshi),
    DK(R.string.dk, R.drawable.ic_room_dk, R.color.dk);

    @StringRes
    private final int name;

    @DrawableRes
    private final int iconRes;

    @ColorRes
    private final int colorRes;

    Room(@StringRes int name, @DrawableRes int iconRes, int colorRes) {
        this.name = name;
        this.iconRes = iconRes;
        this.colorRes = colorRes;
    }

    @StringRes
    public int getStringResName() {
        return name;
    }

    @DrawableRes
    public int getDrawableResIcon() {
        return iconRes;
    }

    @ColorRes
    public int getColorRes() {
        return colorRes;
    }
}

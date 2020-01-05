package fr.delcey.mareu.ui.utils;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class ClickChildViewWithId implements ViewAction {

    @IdRes
    private final int viewId;

    public ClickChildViewWithId(@IdRes int viewId) {
        this.viewId = viewId;
    }

    @Override
    public Matcher<View> getConstraints() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Click on a child view with specified id : " + viewId;
    }

    @Override
    public void perform(UiController uiController, View view) {
        View v = view.findViewById(viewId);

        v.performClick();
    }
}

package fr.delcey.mareu.ui.utils;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.StringRes;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class EditTextErrorMatcher extends TypeSafeMatcher<View> {

    @StringRes
    private final int expectedErrorStringRes;

    public EditTextErrorMatcher(@StringRes int expectedErrorStringRes) {
        this.expectedErrorStringRes = expectedErrorStringRes;
    }

    @Override
    public boolean matchesSafely(View view) {
        if (!(view instanceof EditText)) {
            return false;
        }

        CharSequence error = ((EditText) view).getError();

        if (error == null) {
            return expectedErrorStringRes == 0;
        }

        String expectedError = view.getContext().getString(expectedErrorStringRes);

        return expectedError.equals(error.toString());
    }

    @Override
    public void describeTo(Description description) {
    }
}
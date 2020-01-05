package fr.delcey.mareu.ui.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DrawableMatcher extends TypeSafeMatcher<View> {

    @DrawableRes
    private final int expectedId;
    private String resourceName;

    public DrawableMatcher(@DrawableRes int expectedId) {
        super(View.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(View target) {
        Resources resources = target.getContext().getResources();
        resourceName = resources.getResourceEntryName(expectedId);

        if (!(target instanceof ImageView)) {
            return false;
        }

        ImageView imageView = (ImageView) target;

        if (expectedId == -1) {
            return imageView.getDrawable() == null;
        }

        Drawable expectedDrawable = resources.getDrawable(expectedId);

        if (expectedDrawable == null) {
            return false;
        }

        Bitmap bitmap = getBitmap(imageView.getDrawable());
        Bitmap otherBitmap = getBitmap(expectedDrawable);

        return bitmap.sameAs(otherBitmap);
    }

    private Bitmap getBitmap(@NonNull Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(expectedId);
        if (resourceName != null) {
            description.appendText("[");
            description.appendText(resourceName);
            description.appendText("]");
        }
    }
}
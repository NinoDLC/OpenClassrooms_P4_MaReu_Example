package fr.delcey.mareu;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainApplication extends Application {

    private static Application application;

    @SuppressWarnings("unused")
    public MainApplication() {
        application = this;
    }

    public static Application getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidThreeTen.init(this);
    }
}

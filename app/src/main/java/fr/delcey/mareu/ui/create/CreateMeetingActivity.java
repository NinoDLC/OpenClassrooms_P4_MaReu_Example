package fr.delcey.mareu.ui.create;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.delcey.mareu.R;

public class CreateMeetingActivity extends AppCompatActivity {

    public static Intent navigate(Context context) {
        return new Intent(context, CreateMeetingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_meeting_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.create_meeting_container, CreateMeetingFragment.newInstance())
                .commit();
        }
    }
}

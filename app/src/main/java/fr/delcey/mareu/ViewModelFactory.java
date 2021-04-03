package fr.delcey.mareu;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.time.Clock;

import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.ui.create.CreateMeetingViewModel;
import fr.delcey.mareu.ui.details.MeetingDetailViewModel;
import fr.delcey.mareu.ui.meetings.MeetingViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
                        new MeetingRepository()
                    );
                }
            }
        }

        return factory;
    }

    @NonNull
    private final MeetingRepository meetingRepository;

    private ViewModelFactory(@NonNull MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MeetingViewModel.class)) {
            return (T) new MeetingViewModel(
                MainApplication.getInstance().getResources(),
                meetingRepository
            );
        } else if (modelClass.isAssignableFrom(CreateMeetingViewModel.class)) {
            return (T) new CreateMeetingViewModel(
                MainApplication.getInstance().getResources(),
                meetingRepository,
                Clock.systemDefaultZone()
            );
        } else if (modelClass.isAssignableFrom(MeetingDetailViewModel.class)) {
            return (T) new MeetingDetailViewModel(
                MainApplication.getInstance(),
                MainApplication.getInstance().getResources(),
                meetingRepository,
                Clock.systemDefaultZone()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
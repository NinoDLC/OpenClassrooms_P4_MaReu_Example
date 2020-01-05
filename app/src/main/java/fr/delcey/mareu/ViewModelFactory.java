package fr.delcey.mareu;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.threeten.bp.Clock;

import fr.delcey.mareu.domain.MeetingRepository;
import fr.delcey.mareu.ui.create.CreateMeetingViewModel;
import fr.delcey.mareu.ui.meetings.MeetingViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    private ViewModelFactory() {
    }

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }

        return factory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MeetingViewModel.class)) {
            return (T) new MeetingViewModel(
                MainApplication.getInstance().getResources(),
                MeetingRepository.getInstance()
            );
        } else if (modelClass.isAssignableFrom(CreateMeetingViewModel.class)) {
            return (T) new CreateMeetingViewModel(
                MainApplication.getInstance().getResources(),
                MeetingRepository.getInstance(),
                Clock.systemDefaultZone()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
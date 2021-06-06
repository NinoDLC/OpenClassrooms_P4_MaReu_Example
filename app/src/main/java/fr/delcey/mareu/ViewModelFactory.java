package fr.delcey.mareu;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.time.Clock;
import java.time.format.DateTimeFormatter;

import fr.delcey.mareu.data.meeting.MeetingRepository;
import fr.delcey.mareu.data.sorting.SortingParametersRepository;
import fr.delcey.mareu.ui.create.CreateMeetingViewModel;
import fr.delcey.mareu.ui.details.MeetingDetailViewModel;
import fr.delcey.mareu.ui.meetings.MeetingViewModel;
import fr.delcey.mareu.ui.meetings.sort.SortViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
                        new MeetingRepository(),
                        new SortingParametersRepository(),
                        DateTimeFormatter.ofPattern("HH:mm")
                    );
                }
            }
        }

        return factory;
    }

    @NonNull
    private final MeetingRepository meetingRepository;
    @NonNull
    private final SortingParametersRepository sortingParametersRepository;
    @NonNull
    private final DateTimeFormatter hourDateTimeFormatter;

    private ViewModelFactory(
        @NonNull MeetingRepository meetingRepository,
        @NonNull SortingParametersRepository sortingParametersRepository,
        @NonNull DateTimeFormatter hourDateTimeFormatter
    ) {
        this.meetingRepository = meetingRepository;
        this.sortingParametersRepository = sortingParametersRepository;
        this.hourDateTimeFormatter = hourDateTimeFormatter;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MeetingViewModel.class)) {
            return (T) new MeetingViewModel(
                MainApplication.getInstance().getResources(),
                meetingRepository,
                sortingParametersRepository
            );
        } else if (modelClass.isAssignableFrom(CreateMeetingViewModel.class)) {
            return (T) new CreateMeetingViewModel(
                MainApplication.getInstance().getResources(),
                meetingRepository,
                hourDateTimeFormatter,
                Clock.systemDefaultZone()
            );
        } else if (modelClass.isAssignableFrom(MeetingDetailViewModel.class)) {
            return (T) new MeetingDetailViewModel(
                MainApplication.getInstance(),
                MainApplication.getInstance().getResources(),
                meetingRepository,
                Clock.systemDefaultZone()
            );
        }else if (modelClass.isAssignableFrom(SortViewModel.class)) {
            return (T) new SortViewModel(
                sortingParametersRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
package fr.delcey.mareu.ui.meetings.sort;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import fr.delcey.mareu.data.sorting.SortingParametersRepository;
import fr.delcey.mareu.data.sorting.model.AlphabeticalSortingType;
import fr.delcey.mareu.data.sorting.model.ChronologicalSortingType;

public class SortViewModel extends ViewModel {

    @NonNull
    private final SortingParametersRepository sortingParametersRepository;

    private final MediatorLiveData<SortViewState> sortViewStateMediatorLiveData = new MediatorLiveData<>();

    public SortViewModel(@NonNull SortingParametersRepository sortingParametersRepository) {
        this.sortingParametersRepository = sortingParametersRepository;

        LiveData<AlphabeticalSortingType> alphabeticalSortingTypeLiveData = sortingParametersRepository.getAlphabeticalSortingTypeLiveData();
        LiveData<ChronologicalSortingType> chronologicalSortingTypeLiveData = sortingParametersRepository.getChronologicalSortingTypeLiveData();

        sortViewStateMediatorLiveData.addSource(alphabeticalSortingTypeLiveData, alphabeticalSortingType ->
            combine(alphabeticalSortingType, chronologicalSortingTypeLiveData.getValue())
        );

        sortViewStateMediatorLiveData.addSource(chronologicalSortingTypeLiveData, chronologicalSortingType ->
            combine(alphabeticalSortingTypeLiveData.getValue(), chronologicalSortingType)
        );
    }

    public void onAlphabeticalSortingClicked() {
        sortingParametersRepository.changeAlphabeticalSorting();
    }

    public void onChronologicalSortingClicked() {
        sortingParametersRepository.changeChronologicalSorting();
    }

    @NonNull
    public LiveData<SortViewState> getViewStateLiveData() {
        return sortViewStateMediatorLiveData;
    }

    private void combine(
        @Nullable AlphabeticalSortingType alphabeticalSortingType,
        @Nullable ChronologicalSortingType chronologicalSortingType
    ) {
        if (alphabeticalSortingType == null || chronologicalSortingType == null) {
            throw new IllegalStateException("At least one sorting type is NULL ! " +
                "alphabeticalSortingType = " + alphabeticalSortingType + "," +
                "chronologicalSortingType = " + chronologicalSortingType);
        }

        sortViewStateMediatorLiveData.setValue(
            new SortViewState(
                alphabeticalSortingType.getState(),
                alphabeticalSortingType.getMessageStringRes(),
                chronologicalSortingType.getState(),
                chronologicalSortingType.getMessageStringRes()
            )
        );
    }
}

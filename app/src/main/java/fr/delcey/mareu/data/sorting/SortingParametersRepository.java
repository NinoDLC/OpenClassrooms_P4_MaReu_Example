package fr.delcey.mareu.data.sorting;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.delcey.mareu.data.sorting.model.AlphabeticalSortingType;
import fr.delcey.mareu.data.sorting.model.ChronologicalSortingType;

public class SortingParametersRepository {

    private final MutableLiveData<AlphabeticalSortingType> alphabeticalSortingTypeMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<ChronologicalSortingType> chronologicalSortingTypeMutableLiveData = new MutableLiveData<>();

    public SortingParametersRepository() {
        alphabeticalSortingTypeMutableLiveData.setValue(AlphabeticalSortingType.NONE);
        chronologicalSortingTypeMutableLiveData.setValue(ChronologicalSortingType.NONE);
    }

    public void changeAlphabeticalSorting() {
        AlphabeticalSortingType type = alphabeticalSortingTypeMutableLiveData.getValue();

        AlphabeticalSortingType newType;

        if (type == AlphabeticalSortingType.AZ) {
            newType = AlphabeticalSortingType.ZA;
        } else if (type == AlphabeticalSortingType.ZA) {
            newType = AlphabeticalSortingType.NONE;
        } else {
            newType = AlphabeticalSortingType.AZ;
        }

        alphabeticalSortingTypeMutableLiveData.setValue(newType);
    }

    public void changeChronologicalSorting() {
        ChronologicalSortingType type = chronologicalSortingTypeMutableLiveData.getValue();

        ChronologicalSortingType newType;

        if (type == ChronologicalSortingType.OLDEST_FIRST) {
            newType = ChronologicalSortingType.NEWEST_FIRST;
        } else if (type == ChronologicalSortingType.NEWEST_FIRST) {
            newType = ChronologicalSortingType.NONE;
        } else {
            newType = ChronologicalSortingType.OLDEST_FIRST;
        }

        chronologicalSortingTypeMutableLiveData.setValue(newType);
    }

    @NonNull
    public LiveData<AlphabeticalSortingType> getAlphabeticalSortingTypeLiveData() {
        return alphabeticalSortingTypeMutableLiveData;
    }

    @NonNull
    public LiveData<ChronologicalSortingType> getChronologicalSortingTypeLiveData() {
        return chronologicalSortingTypeMutableLiveData;
    }
}

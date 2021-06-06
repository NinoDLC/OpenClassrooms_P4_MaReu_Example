package fr.delcey.mareu.data.sorting;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.delcey.mareu.data.sorting.model.AlphabeticalSortingType;
import fr.delcey.mareu.data.sorting.model.ChronologicalSortingType;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;

public class SortingParametersRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SortingParametersRepository sortingParametersRepository;

    @Before
    public void setUp() {
        sortingParametersRepository = new SortingParametersRepository();
    }

    @Test
    public void initial_state() throws InterruptedException {
        // When
        AlphabeticalSortingType alphabeticalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getAlphabeticalSortingTypeLiveData()
        );
        ChronologicalSortingType chronologicalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getChronologicalSortingTypeLiveData()
        );

        // Then
        assertEquals(AlphabeticalSortingType.NONE, alphabeticalSortingType);
        assertEquals(ChronologicalSortingType.NONE, chronologicalSortingType);
    }

    @Test
    public void changeAlphabeticalSortingType() throws InterruptedException {
        // Given
        sortingParametersRepository.changeAlphabeticalSorting();

        // When
        AlphabeticalSortingType alphabeticalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getAlphabeticalSortingTypeLiveData()
        );

        // Then
        assertEquals(AlphabeticalSortingType.AZ, alphabeticalSortingType);
    }

    @Test
    public void changeAlphabeticalSortingTypeTwice() throws InterruptedException {
        // Given
        sortingParametersRepository.changeAlphabeticalSorting();
        sortingParametersRepository.changeAlphabeticalSorting();

        // When
        AlphabeticalSortingType alphabeticalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getAlphabeticalSortingTypeLiveData()
        );

        // Then
        assertEquals(AlphabeticalSortingType.ZA, alphabeticalSortingType);
    }

    @Test
    public void changeAlphabeticalSortingTypeThrice() throws InterruptedException {
        // Given
        sortingParametersRepository.changeAlphabeticalSorting();
        sortingParametersRepository.changeAlphabeticalSorting();
        sortingParametersRepository.changeAlphabeticalSorting();

        // When
        AlphabeticalSortingType alphabeticalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getAlphabeticalSortingTypeLiveData()
        );

        // Then
        assertEquals(AlphabeticalSortingType.NONE, alphabeticalSortingType);
    }


    @Test
    public void changeChronologicalSortingType() throws InterruptedException {
        // Given
        sortingParametersRepository.changeChronologicalSorting();

        // When
        ChronologicalSortingType chronologicalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getChronologicalSortingTypeLiveData()
        );

        // Then
        assertEquals(ChronologicalSortingType.OLDEST_FIRST, chronologicalSortingType);
    }

    @Test
    public void changeChronologicalSortingTypeTwice() throws InterruptedException {
        // Given
        sortingParametersRepository.changeChronologicalSorting();
        sortingParametersRepository.changeChronologicalSorting();

        // When
        ChronologicalSortingType chronologicalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getChronologicalSortingTypeLiveData()
        );

        // Then
        assertEquals(ChronologicalSortingType.NEWEST_FIRST, chronologicalSortingType);
    }

    @Test
    public void changeChronologicalSortingTypeThrice() throws InterruptedException {
        // Given
        sortingParametersRepository.changeChronologicalSorting();
        sortingParametersRepository.changeChronologicalSorting();
        sortingParametersRepository.changeChronologicalSorting();

        // When
        ChronologicalSortingType chronologicalSortingType = LiveDataTestUtils.getOrAwaitValue(
            sortingParametersRepository.getChronologicalSortingTypeLiveData()
        );

        // Then
        assertEquals(ChronologicalSortingType.NONE, chronologicalSortingType);
    }
}
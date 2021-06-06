package fr.delcey.mareu.ui.meetings.sort;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fr.delcey.mareu.data.sorting.SortingParametersRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SortViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private SortingParametersRepository sortingParametersRepository;

    private SortViewModel viewModel;

    @Before
    public void setUp() {
        given(sortingParametersRepository.getAlphabeticalSortingTypeLiveData()).willReturn(new MutableLiveData<>());
        given(sortingParametersRepository.getChronologicalSortingTypeLiveData()).willReturn(new MutableLiveData<>());

        viewModel = new SortViewModel(sortingParametersRepository);
    }

    @Test
    public void verify_alphabetical_sorting_click() {
        // When
        viewModel.onAlphabeticalSortingClicked();

        // Then
        verify(sortingParametersRepository).getAlphabeticalSortingTypeLiveData();
        verify(sortingParametersRepository).getChronologicalSortingTypeLiveData();
        verify(sortingParametersRepository).changeAlphabeticalSorting();
        verifyNoMoreInteractions(sortingParametersRepository);
    }

    @Test
    public void verify_chronological_sorting_click() {
        // When
        viewModel.onChronologicalSortingClicked();

        // Then
        verify(sortingParametersRepository).getAlphabeticalSortingTypeLiveData();
        verify(sortingParametersRepository).getChronologicalSortingTypeLiveData();
        verify(sortingParametersRepository).changeChronologicalSorting();
        verifyNoMoreInteractions(sortingParametersRepository);
    }
}
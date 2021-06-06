package fr.delcey.mareu.ui.meetings.sort;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import fr.delcey.mareu.data.sorting.SortingParametersRepository;
import fr.delcey.mareu.data.sorting.model.AlphabeticalSortingType;
import fr.delcey.mareu.data.sorting.model.ChronologicalSortingType;
import fr.delcey.mareu.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.BDDMockito.given;

@RunWith(Parameterized.class)
public class SortViewModelParameterizedTest {

    @Parameters(name = "AlphabeticalSortingType: {0}, ChronologicalSortingType: {1}")
    public static Object[] data() {
        return new Object[][]{
            {AlphabeticalSortingType.NONE, ChronologicalSortingType.NONE},
            {AlphabeticalSortingType.NONE, ChronologicalSortingType.OLDEST_FIRST},
            {AlphabeticalSortingType.NONE, ChronologicalSortingType.NEWEST_FIRST},

            {AlphabeticalSortingType.AZ, ChronologicalSortingType.NONE},
            {AlphabeticalSortingType.AZ, ChronologicalSortingType.OLDEST_FIRST},
            {AlphabeticalSortingType.AZ, ChronologicalSortingType.NEWEST_FIRST},

            {AlphabeticalSortingType.ZA, ChronologicalSortingType.NONE},
            {AlphabeticalSortingType.ZA, ChronologicalSortingType.OLDEST_FIRST},
            {AlphabeticalSortingType.ZA, ChronologicalSortingType.NEWEST_FIRST},
        };
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final SortingParametersRepository sortingParametersRepository = Mockito.mock(SortingParametersRepository.class);

    private MutableLiveData<AlphabeticalSortingType> alphabeticalSortingTypeMutableLiveData;
    private MutableLiveData<ChronologicalSortingType> chronologicalSortingTypeMutableLiveData;

    private SortViewModel viewModel;

    @Parameter()
    public AlphabeticalSortingType alphabeticalSortingType;
    @Parameter(1)
    public ChronologicalSortingType chronologicalSortingType;

    @Before
    public void setUp() {
        alphabeticalSortingTypeMutableLiveData = new MutableLiveData<>();
        chronologicalSortingTypeMutableLiveData = new MutableLiveData<>();

        given(sortingParametersRepository.getAlphabeticalSortingTypeLiveData()).willReturn(alphabeticalSortingTypeMutableLiveData);
        given(sortingParametersRepository.getChronologicalSortingTypeLiveData()).willReturn(chronologicalSortingTypeMutableLiveData);

        viewModel = new SortViewModel(sortingParametersRepository);
    }

    @Test
    public void test() throws InterruptedException {
        // Given
        alphabeticalSortingTypeMutableLiveData.setValue(alphabeticalSortingType);
        chronologicalSortingTypeMutableLiveData.setValue(chronologicalSortingType);

        // When
        SortViewState result = LiveDataTestUtils.getOrAwaitValue(viewModel.getViewStateLiveData());

        // Then
        assertEquals(
            new SortViewState(
                alphabeticalSortingType.getState(),
                alphabeticalSortingType.getMessageStringRes(),
                chronologicalSortingType.getState(),
                chronologicalSortingType.getMessageStringRes()
            ),
            result
        );
    }
}
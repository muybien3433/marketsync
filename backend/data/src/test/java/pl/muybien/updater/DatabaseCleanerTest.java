package pl.muybien.updater;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.muybien.entity.Finance;
import pl.muybien.repository.FinanceRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseCleanerTest {

    @Mock
    private FinanceRepository repository;

    @Mock
    private Finance finance1;

    @Mock
    private Finance finance2;

    @InjectMocks
    private DatabaseCleaner databaseCleaner;

    @Test
    void cleanOldFinanceDetails_ShouldDoNothing_WhenNoFinancesExist() {
        when(repository.findAll()).thenReturn(List.of());

        databaseCleaner.updateAssets();

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void cleanOldFinanceDetails_ShouldCleanAndSave_WhenSingleFinanceExists() {
        when(repository.findAll()).thenReturn(List.of(finance1));

        databaseCleaner.updateAssets();

        verify(repository).findAll();
        verify(finance1).cleanOldFinanceDetails();
        verify(repository).save(finance1);
    }

    @Test
    void cleanOldFinanceDetails_ShouldCleanAndSaveAll_WhenMultipleFinancesExist() {
        when(repository.findAll()).thenReturn(List.of(finance1, finance2));

        databaseCleaner.updateAssets();

        verify(repository).findAll();
        verify(finance1).cleanOldFinanceDetails();
        verify(finance2).cleanOldFinanceDetails();
        verify(repository).save(finance1);
        verify(repository).save(finance2);
    }

    @Test
    void cleanOldFinanceDetails_ShouldHandleEmptyFinanceDetailsGracefully() {
        Finance emptyFinance = mock(Finance.class);
        when(repository.findAll()).thenReturn(List.of(emptyFinance));

        databaseCleaner.updateAssets();

        verify(emptyFinance).cleanOldFinanceDetails();
        verify(repository).save(emptyFinance);
    }
}
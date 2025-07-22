package gift.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.entity.Option;
import gift.entity.Product;
import gift.exception.InvalidQuantityException;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private OptionService optionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void subtractQuantityValidCase() {
        Product product = new Product("Test Product", 1000, "test.com");
        Option option = new Option(product, "Test Option", 100);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(optionRepository.save(any(Option.class))).thenReturn(option);

        optionService.subtractOptionQuantity(1L, 60);

        verify(optionRepository).findById(1L);
        verify(optionRepository).save(option);
        assertEquals(40, option.getQuantity());
    }

    @Test
    void subtractQuantityNotFound() {
        when(optionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OptionNotFoundException.class, () -> {
            optionService.subtractOptionQuantity(1L, 60);
        });

        verify(optionRepository).findById(1L);
        verify(optionRepository, never()).save(any(Option.class));
    }

    @Test
    void subtractQuantityExceeds() {
        Product product = new Product("Test Product", 1000, "test.com");
        Option option = new Option(product, "Test Option", 100);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        assertThrows(InvalidQuantityException.class, () -> {
            optionService.subtractOptionQuantity(1L, 101);
        });

        verify(optionRepository).findById(1L);
        verify(optionRepository, never()).save(any(Option.class));
    }

}

package unit.com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.BookingUpdateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingUpdateDtoTests {
    @Spy
    private BookingUpdateDto dto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validate_ValidDto_CallsAllValidateMethods() {
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        Assertions.assertTrue(dto.validate());
        Mockito.verify(dto, Mockito.times(1)).validateDates();
    }

    @Test
    void validateDates_NullStartDate_ThrowsException() {
        dto.setStartDate(null);
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("Start date is required", exception.getMessage());
    }

    @Test
    void validateDates_NullEndDate_ThrowsException() {
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(null);

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("End date is required", exception.getMessage());
    }

    @Test
    void validateDates_StartDateAfterEndDate_ThrowsException() {
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now());

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void validateDates_StartDateEqualsEndDate_ThrowsException() {
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("Start date must be different than end date", exception.getMessage());
    }
}

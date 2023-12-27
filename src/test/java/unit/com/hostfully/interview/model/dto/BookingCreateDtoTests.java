package unit.com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.PropertyCreateDto;
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

public class BookingCreateDtoTests {
    @Spy
    private BookingCreateDto dto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validate_ValidDto_CallsAllValidateMethods() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        Assertions.assertTrue(dto.validate());
        Mockito.verify(dto, Mockito.times(1)).validatePropertyId();
        Mockito.verify(dto, Mockito.times(1)).validateDates();
    }

    @Test
    void validatePropertyId_InvalidPropertyId_ThrowsException() {
        dto.setPropertyId("invalid-id");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validatePropertyId());
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void validatePropertyId_NullPropertyId_ThrowsException() {
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validatePropertyId());
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void validatePropertyId_EmptyPropertyId_ThrowsException() {
        dto.setPropertyId("");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validatePropertyId());
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void validateDates_NullStartDate_ThrowsException() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(null);
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("Start date is required", exception.getMessage());
    }

    @Test
    void validateDates_NullEndDate_ThrowsException() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(null);

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("End date is required", exception.getMessage());
    }

    @Test
    void validateDates_StartDateAfterEndDate_ThrowsException() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now());

        var exception = assertThrows(BadRequestException.class, () -> dto.validateDates());
        assertEquals("Start date must be before end date", exception.getMessage());
    }
}

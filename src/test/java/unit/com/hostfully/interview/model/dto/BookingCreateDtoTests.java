package unit.com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingCreateDtoTests {
    @Spy
    private BookingCreateDto dto;

    private List<GuestCreateDTO> guests = List.of(new GuestCreateDTO("John"));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validate_ValidDto_CallsAllValidateMethods() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setGuests(guests);

        Assertions.assertTrue(dto.validate());
        Mockito.verify(dto, Mockito.times(1)).validatePropertyId();
        Mockito.verify(dto, Mockito.times(1)).validateDates();
        Mockito.verify(dto, Mockito.times(1)).validateGuests();
    }

    @Test
    void validatePropertyId_InvalidPropertyId_ThrowsException() {
        dto.setPropertyId("invalid-id");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validatePropertyId());
        assertEquals("Property ID is required and must be a valid UUID", exception.getMessage());
    }

    @Test
    void validatePropertyId_NullPropertyId_ThrowsException() {
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validatePropertyId());
        assertEquals("Property ID is required and must be a valid UUID", exception.getMessage());
    }

    @Test
    void validatePropertyId_EmptyPropertyId_ThrowsException() {
        dto.setPropertyId("");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> dto.validatePropertyId());
        assertEquals("Property ID is required and must be a valid UUID", exception.getMessage());
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

    @Test
    void validateGuests_NullGuests_ThrowsException() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setGuests(null);

        var exception = assertThrows(BadRequestException.class, () -> dto.validateGuests());
        assertEquals("Guests is required", exception.getMessage());
    }
}

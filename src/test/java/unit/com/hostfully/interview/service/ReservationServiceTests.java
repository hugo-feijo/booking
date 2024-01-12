package unit.com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.repository.BlockRepository;
import com.hostfully.interview.repository.BookingRepository;
import com.hostfully.interview.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTests {

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateIfDatesAreAvailable_DateAlreadyBooked_ThrowsException() {
        var propertyId = UUID.randomUUID().toString();
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(propertyId, null, startDate, endDate)).thenReturn(true);
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(propertyId, null, startDate, endDate)).thenReturn(false);

        var exception = assertThrows(BadRequestException.class, () -> reservationService.validateIfDatesAreAvailable(propertyId, startDate, endDate));
        assertEquals("Dates are already booked", exception.getMessage());
    }

    @Test
    void validateIfDatesAreAvailable_NoBookingAndBlockFound_ReturnsTrue() {
        var propertyId = UUID.randomUUID().toString();
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(propertyId, null,  startDate, endDate)).thenReturn(false);
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(propertyId, null, startDate, endDate)).thenReturn(false);

        var result = reservationService.validateIfDatesAreAvailable(propertyId, startDate, endDate);
        assertTrue(result);
    }

    @Test
    void validateIfDatesAreAvailable_DateAlreadyBlocked_ThrowsException() {
        var propertyId = UUID.randomUUID().toString();
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(propertyId, null, startDate, endDate)).thenReturn(false);
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(propertyId, null, startDate, endDate)).thenReturn(true);

        var exception = assertThrows(BadRequestException.class, () -> reservationService.validateIfDatesAreAvailable(propertyId, startDate, endDate));
        assertEquals("Dates are already blocked", exception.getMessage());
    }

}

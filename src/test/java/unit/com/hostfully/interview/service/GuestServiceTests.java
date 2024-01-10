package unit.com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Guest;
import com.hostfully.interview.service.BookingService;
import com.hostfully.interview.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GuestServiceTests {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private GuestService guestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGuest_InvalidateBookingId_ThrowsException() {
        var bookingId = "invalid-booking-id";
        Mockito.when(bookingService.getBooking(bookingId)).thenThrow(new BadRequestException("Invalid booking id"));


        var exception = assertThrows(BadRequestException.class, () -> guestService.createGuest(bookingId, null));
        assertEquals("Invalid booking id", exception.getMessage());
    }

    @Test
    void createGuest_NullGuest_ThrowsException() {
        var bookingId = UUID.randomUUID().toString();
        Mockito.when(bookingService.getBooking(bookingId)).thenReturn(new Booking());

        var exception = assertThrows(BadRequestException.class, () -> guestService.createGuest(bookingId, null));
        assertEquals("Guest is required", exception.getMessage());
    }

    @Test
    void createGuest_EmptyGuestName_ThrowsException() {
        var bookingId = UUID.randomUUID().toString();
        Mockito.when(bookingService.getBooking(bookingId)).thenReturn(new Booking());

        var exception = assertThrows(BadRequestException.class, () -> guestService.createGuest(bookingId, new GuestCreateDTO(null)));
        assertEquals("Guest name is required", exception.getMessage());
    }

    @Test
    void createGuest_ValidGuest_ReturnsBooking() {
        var bookingId = UUID.randomUUID().toString();
        var booking = new Booking();
        booking.setGuests(new ArrayList<>(List.of(new Guest(UUID.randomUUID(), "John Doe", LocalDate.now(), null))));
        Mockito.when(bookingService.getBooking(bookingId)).thenReturn(booking);
        Mockito.when(bookingService.saveBooking(booking)).thenReturn(booking);

        var guestCreateDTO = new GuestCreateDTO("Alan Wake");
        var result = guestService.createGuest(bookingId, guestCreateDTO);

        assertEquals(booking, result);
        assertEquals(2, result.getGuests().size());
        assertEquals("Alan Wake", result.getGuests().get(1).getName());
    }

}

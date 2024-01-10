package unit.com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Guest;
import com.hostfully.interview.repository.GuestRepository;
import com.hostfully.interview.service.BookingService;
import com.hostfully.interview.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GuestServiceTests {

    @Mock
    private BookingService bookingService;

    @Mock
    private GuestRepository guestRepository;

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

    @Test
    void updateGuest_NullGuestId_ThrowsException() {
        var exception = assertThrows(BadRequestException.class, () -> guestService.updateGuest(null, null));
        assertEquals("Guest id is required", exception.getMessage());
    }

    @Test
    void getGuest_InvalidGuestId_ThrowsException() {
        var exception = assertThrows(BadRequestException.class, () -> guestService.updateGuest("invalid-guest-id", null));
        assertEquals("Invalid guest id", exception.getMessage());
    }

    @Test
    void updateGuest_GuestNotFound_ThrowsException() {
        var guestId = UUID.randomUUID().toString();
        Mockito.when(guestRepository.findById(UUID.fromString(guestId))).thenReturn(Optional.empty());

        var exception = assertThrows(BadRequestException.class, () -> guestService.updateGuest(guestId, null));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void updateGuest_ValidGuestId_ReturnsGuest() {
        var guestId = UUID.randomUUID().toString();
        var guest = new Guest(UUID.randomUUID(), "John Doe", LocalDate.now(), null);
        Mockito.when(guestRepository.findById(UUID.fromString(guestId))).thenReturn(Optional.of(guest));
        Mockito.when(guestRepository.save(guest)).thenReturn(guest);

        var result = guestService.updateGuest(guestId, new GuestCreateDTO("Alan Wake"));

        assertEquals("Alan Wake", result.getName());
    }

    @Test
    void deleteGuest_NullGuestId_ThrowsException() {
        var exception = assertThrows(BadRequestException.class, () -> guestService.deleteGuest(UUID.randomUUID().toString(), null));
        assertEquals("Guest id is required", exception.getMessage());
    }

    @Test
    void deleteGuest_NullBookingId_ThrowsException() {
        var guestId = UUID.randomUUID();
        Mockito.when(guestRepository.findById(guestId)).thenReturn(Optional.of(new Guest()));
        Mockito.when(bookingService.getBooking(null)).thenThrow(new BadRequestException("Booking id is required"));
        var exception = assertThrows(BadRequestException.class, () -> guestService.deleteGuest(null, guestId.toString()));

        assertEquals("Booking id is required", exception.getMessage());
    }

    @Test
    void deleteGuest_OnlyOneGuest_ThrowsException() {
        var guestId = UUID.randomUUID();
        var bookingId = UUID.randomUUID();
        var booking = new Booking();
        booking.setId(bookingId);
        booking.setGuests(List.of(new Guest(guestId, "John Doe", LocalDate.now(), null)));
        Mockito.when(guestRepository.findById(guestId)).thenReturn(Optional.of(new Guest()));
        Mockito.when(bookingService.getBooking(bookingId.toString())).thenReturn(booking);

        var exception = assertThrows(BadRequestException.class, () -> guestService.deleteGuest(bookingId.toString(), guestId.toString()));

        assertEquals("Booking must have at least one guest", exception.getMessage());
    }

}

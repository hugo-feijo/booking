package unit.com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.BookingUpdateDto;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.model.entity.Guest;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.repository.BookingRepository;
import com.hostfully.interview.service.BookingService;
import com.hostfully.interview.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTests {

    @Mock
    private PropertyService propertyService;

    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private BookingCreateDto dto;

    @Spy
    private BookingUpdateDto updateDto;

    @InjectMocks
    private BookingService bookingService;

    private final Property property = new Property(UUID.randomUUID(), "Property Name");
    private final List<Guest> guests = List.of(new Guest(UUID.randomUUID(), "John", LocalDate.now(), null));
    private final List<GuestCreateDTO> guestCreateDTOS = List.of(new GuestCreateDTO("John"));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_InvalidPropertyId_ThrowsException() {
        dto.setPropertyId("invalid-property-id");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        Mockito.when(propertyService.getProperty(dto.getPropertyId())).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto));
        assertEquals("Property ID is required and must be a valid UUID", exception.getMessage());

        Mockito.verify(dto, Mockito.times(1)).validate();
    }

    @Test
    void validateIfDatesAreAvailable_DateAlreadyBooked_ThrowsException() {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setGuests(guestCreateDTOS);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(dto.getPropertyId(), null, startDate, endDate)).thenReturn(true);

        var exception = assertThrows(BadRequestException.class, () -> bookingService.validateIfDatesAreAvailable(dto.getPropertyId(), null, startDate, endDate));
        assertEquals("Dates already booked", exception.getMessage());
    }

    @Test
    void validateIfDatesAreAvailable_NoBookingFound_ReturnsTrue() {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setGuests(guestCreateDTOS);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(dto.getPropertyId(), null,  startDate, endDate)).thenReturn(false);

        var result = bookingService.validateIfDatesAreAvailable(dto.getPropertyId(), null,  startDate, endDate);
        assertTrue(result);
    }

    @Test
    void createBooking_ValidDto_ReturnsEntity() {
        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var booking = new Booking(UUID.randomUUID(), property, startDate, endDate, BookingStatus.PENDING, LocalDate.now(), null, guests);
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setGuests(guestCreateDTOS);

        Mockito.when(propertyService.getProperty(dto.getPropertyId())).thenReturn(property);
        Mockito.when(bookingRepository.save(ArgumentMatchers.any(Booking.class))).thenReturn(booking);
        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(dto.getPropertyId(), null, startDate, endDate)).thenReturn(false);

        var newBooking = bookingService.createBooking(dto);

        Mockito.verify(dto, Mockito.times(1)).validate();
        Mockito.verify(bookingRepository, Mockito.times(1)).existByPropertyIdAndDateRange(dto.getPropertyId(), null, startDate, endDate);
        assertEquals(property, newBooking.getProperty());
        assertEquals(dto.getStartDate(), newBooking.getStartDate());
        assertEquals(dto.getEndDate(), newBooking.getEndDate());
        assertNotNull(newBooking.getId());
        assertNotNull(newBooking.getCreatedAt());
    }

    @Test
    void bookingCreateDtoToBooking_ValidDto_ReturnsNewBooking() {
        dto.setPropertyId(property.getId().toString());
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setGuests(guestCreateDTOS);
        Mockito.when(propertyService.getProperty(dto.getPropertyId())).thenReturn(property);

        var booking = bookingService.bookingCreateDtoToBooking(dto);
        assertNull(booking.getId());
        assertEquals(property, booking.getProperty());
        assertEquals(dto.getStartDate(), booking.getStartDate());
        assertEquals(dto.getEndDate(), booking.getEndDate());
    }

    @Test
    void getBookingsByPropertyId_InvalidPropertyId_ThrowsBadRequest() {
        var propertyId = "invalid-property-id";
        Mockito.when(propertyService.getProperty(propertyId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.getBookingsByPropertyId(propertyId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void getBookingsByPropertyId_ValidPropertyId_ReturnsBookings() {
        var propertyId = UUID.randomUUID().toString();
        Mockito.when(propertyService.getProperty(propertyId)).thenReturn(property);
        Mockito.when(bookingRepository.findAllByProperty(property)).thenReturn(List.of(new Booking()));

        var bookings = bookingService.getBookingsByPropertyId(propertyId);
        assertEquals(1, bookings.size());
    }

    @Test
    void getBooking_InvalidBookingId_ThrowsBadRequest() {
        var bookingId = "invalid-booking-id";
        Mockito.when(propertyService.validUUID(bookingId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.getBooking(bookingId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void getBooking_ValidBookingId_ReturnsBooking() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CONFIRMED, LocalDate.now(), null, guests);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        var result = bookingService.getBooking(bookingId.toString());
        assertEquals(booking, result);
    }

    @Test
    void cancelBooking_InvalidBookingId_ThrowsBadRequest() {
        var bookingId = "invalid-booking-id";
        Mockito.when(propertyService.validUUID(bookingId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(bookingId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void cancelBooking_BookingCancelled_ThrowsBadRequest() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CANCELLED, LocalDate.now(), null, guests);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(bookingId.toString()));
        assertEquals("Booking already cancelled", exception.getMessage());
    }

    @Test
    void cancelBooking_ValidBooking_ReturnsBookingCancelled() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CONFIRMED, LocalDate.now(), null, guests);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        var cancelledBooking = bookingService.cancelBooking(bookingId.toString());
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
    }

    @Test
    void rebookBooking_InvalidBookingId_ThrowsBadRequest() {
        var bookingId = "invalid-booking-id";
        Mockito.when(propertyService.validUUID(bookingId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.rebookBooking(bookingId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void rebookBooking_BookingNotCancelled_ThrowsBadRequest() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CONFIRMED, LocalDate.now(), null, guests);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.rebookBooking(bookingId.toString()));
        assertEquals("Booking not cancelled", exception.getMessage());
    }

    @Test
    void rebookBooking_ValidBooking_ReturnsBookingRebooked() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CANCELLED, LocalDate.now(), null, guests);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        var cancelledBooking = bookingService.rebookBooking(bookingId.toString());
        assertEquals(BookingStatus.CONFIRMED, cancelledBooking.getStatus());
    }

    @Test
    void deleteBooking_InvalidBookingId_ThrowsBadRequest() {
        var bookingId = "invalid-booking-id";
        Mockito.when(propertyService.validUUID(bookingId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.deleteBooking(bookingId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void deleteBooking_ValidBookingId_ReturnsBookingDeleted() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CANCELLED, LocalDate.now(), null, guests);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(bookingId.toString());
        Mockito.verify(bookingRepository, Mockito.times(1)).delete(booking);
    }

    @Test
    void updateBooking_InvalidBookingId_ThrowsBadRequest() {
        var bookingId = "invalid-booking-id";
        updateDto.setStartDate(LocalDate.now());
        updateDto.setEndDate(LocalDate.now().plusDays(1));
        Mockito.when(propertyService.validUUID(bookingId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.updateBooking(bookingId, updateDto));
        assertEquals("Bad Request", exception.getMessage());
        Mockito.verify(updateDto, Mockito.times(1)).validate();
    }

    @Test
    void updateBooking_InvalidDto_ThrowsBadRequest() {
        var bookingId = UUID.randomUUID();
        updateDto.setStartDate(LocalDate.now());
        updateDto.setEndDate(null);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);

        var exception = assertThrows(BadRequestException.class, () -> bookingService.updateBooking(bookingId.toString(), updateDto));
        assertEquals("End date is required", exception.getMessage());
        Mockito.verify(updateDto, Mockito.times(1)).validate();
    }

    @Test
    void updateBooking_ValidBookingId_ReturnsBookingUpdated() {
        var bookingId = UUID.randomUUID();
        var newStartDate = LocalDate.of(2023, 1, 5);
        var newEndDate = LocalDate.of(2023, 1, 15);
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CANCELLED, LocalDate.now(), null, guests);
        updateDto.setStartDate(newStartDate);
        updateDto.setEndDate(newEndDate);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        var updatedBooking = bookingService.updateBooking(bookingId.toString(), updateDto);
        assertEquals(newStartDate, updatedBooking.getStartDate());
        assertEquals(newEndDate, updatedBooking.getEndDate());
    }
}

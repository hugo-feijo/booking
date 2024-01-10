package unit.com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.repository.BookingRepository;
import com.hostfully.interview.service.BookingService;
import com.hostfully.interview.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTests {

    @Mock
    private PropertyService propertyService;

    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private BookingCreateDto dto;

    @InjectMocks
    private BookingService bookingService;

    private final Property property = new Property(UUID.randomUUID(), "Property Name");

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
    void validateBookingDates_DateAlreadyBooked_ThrowsException() {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(dto.getPropertyId(), startDate, endDate)).thenReturn(true);

        var exception = assertThrows(BadRequestException.class, () -> bookingService.validateBookingDates(dto));
        assertEquals("Dates already booked", exception.getMessage());
    }

    @Test
    void validateBookingDates_NoBookingFound_ReturnsTrue() {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(dto.getPropertyId(), startDate, endDate)).thenReturn(false);

        var result = bookingService.validateBookingDates(dto);
        assertTrue(result);
    }

    @Test
    void createBooking_ValidDto_ReturnsEntity() {
        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var booking = new Booking(UUID.randomUUID(), property, startDate, endDate, BookingStatus.PENDING, LocalDate.now(), null);
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        Mockito.when(propertyService.getProperty(dto.getPropertyId())).thenReturn(property);
        Mockito.when(bookingRepository.save(ArgumentMatchers.any(Booking.class))).thenReturn(booking);
        Mockito.when(bookingRepository.existByPropertyIdAndDateRange(dto.getPropertyId(), startDate, endDate)).thenReturn(false);

        var newBooking = bookingService.createBooking(dto);

        Mockito.verify(dto, Mockito.times(1)).validate();
        Mockito.verify(bookingRepository, Mockito.times(1)).existByPropertyIdAndDateRange(dto.getPropertyId(), startDate, endDate);
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
    void cancelBooking_InvalidBookingId_ThrowsBadRequest() {
        var bookingId = "invalid-booking-id";
        Mockito.when(propertyService.validUUID(bookingId)).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(bookingId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void cancelBooking_BookingCancelled_ThrowsBadRequest() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CANCELLED, LocalDate.now(), null);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(bookingId.toString()));
        assertEquals("Booking already cancelled", exception.getMessage());
    }

    @Test
    void cancelBooking_ValidBooking_ReturnsBookingCancelled() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CONFIRMED, LocalDate.now(), null);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(java.util.Optional.of(booking));
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
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CONFIRMED, LocalDate.now(), null);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.rebookBooking(bookingId.toString()));
        assertEquals("Booking not cancelled", exception.getMessage());
    }

    @Test
    void rebokBooking_ValidBooking_ReturnsBookingRebooked() {
        var bookingId = UUID.randomUUID();
        var booking = new Booking(UUID.randomUUID(), property, LocalDate.now(), LocalDate.now().plusDays(1), BookingStatus.CANCELLED, LocalDate.now(), null);
        Mockito.when(propertyService.validUUID(bookingId.toString())).thenReturn(bookingId);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(java.util.Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        var cancelledBooking = bookingService.rebookBooking(bookingId.toString());
        assertEquals(BookingStatus.CONFIRMED, cancelledBooking.getStatus());
    }
}

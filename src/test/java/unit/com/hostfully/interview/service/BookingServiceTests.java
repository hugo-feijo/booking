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

    private Property property = new Property(UUID.randomUUID(), "Property Name");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_NonExistingPropertyId_ThrowsException() {
        dto.setPropertyId(UUID.randomUUID().toString());
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        Mockito.when(propertyService.getProperty(dto.getPropertyId())).thenThrow(new BadRequestException("Bad Request"));

        var exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto));
        assertEquals("Bad Request", exception.getMessage());

        Mockito.verify(dto, Mockito.times(1)).validate();
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

        var newBooking = bookingService.createBooking(dto);

        Mockito.verify(dto, Mockito.times(1)).validate();
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
}

package unit.com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BlockCreateDto;
import com.hostfully.interview.model.entity.Block;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.repository.BlockRepository;
import com.hostfully.interview.service.BlockService;
import com.hostfully.interview.service.BookingService;
import com.hostfully.interview.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BlockServiceTests {

    @Mock
    private PropertyService propertyService;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private BookingService bookingService;

    @Spy
    private BlockCreateDto blockCreateDto;

    @InjectMocks
    private BlockService blockService;

    private final Property property = new Property(UUID.randomUUID(), "property-name");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBlock_InvalidPropertyId_ThrowsException() {
        var propertyId = "invalid-property-id";
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(propertyService.getProperty(propertyId)).thenThrow(new BadRequestException("Invalid property id"));
        var exception = assertThrows(BadRequestException.class, () -> blockService.createBlock(blockCreateDto, propertyId));
        assertEquals("Invalid property id", exception.getMessage());
    }

    @Test
    void createBlock_InvalidDates_ThrowsException() {
        var propertyId = "valid-property-id";
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().minusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> blockService.createBlock(blockCreateDto, propertyId));
        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void createBlock_DatesAlreadyBlocked_ThrowsException() {
        var propertyId = "valid-property-id";
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(propertyService.getProperty(propertyId)).thenReturn(property);
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(propertyId, null, blockCreateDto.getStartDate(), blockCreateDto.getEndDate())).thenReturn(true);
        var exception = assertThrows(BadRequestException.class, () -> blockService.createBlock(blockCreateDto, propertyId));
        assertEquals("Dates already blocked", exception.getMessage());
    }

    @Test
    void createBlock_DatesAlreadyBooked_ThrowsException() {
        var propertyId = "valid-property-id";
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(propertyService.getProperty(propertyId)).thenReturn(property);
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(propertyId, null, blockCreateDto.getStartDate(), blockCreateDto.getEndDate())).thenReturn(false);
        Mockito.when(blockService.validateIfDatesAreAvailable(propertyId, null, blockCreateDto.getStartDate(), blockCreateDto.getEndDate())).thenThrow(new BadRequestException("Dates already booked"));

        var exception = assertThrows(BadRequestException.class, () -> blockService.createBlock(blockCreateDto, propertyId));
        assertEquals("Dates already booked", exception.getMessage());
    }

    @Test
    void createBlock_ValidDates_ReturnsBlock() {
        var propertyId = "valid-property-id";
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(propertyService.getProperty(propertyId)).thenReturn(property);
        Mockito.when(blockRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        var block = blockService.createBlock(blockCreateDto, propertyId);
        assertEquals(property, block.getProperty());
        assertEquals(blockCreateDto.getStartDate(), block.getStartDate());
        assertEquals(blockCreateDto.getEndDate(), block.getEndDate());
    }

    @Test
    void getBlock_InvalidBlockId_ThrowsException() {
        var blockId = "invalid-block-id";

        Mockito.when(propertyService.validUUID(blockId)).thenThrow(new BadRequestException("Bad Request"));
        var exception = assertThrows(BadRequestException.class, () -> blockService.getBlock(blockId));
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void updateBlock_InvalidDates_ThrowsException() {
        var blockId = "valid-block-id";
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().minusDays(1));

        var exception = assertThrows(BadRequestException.class, () -> blockService.updateBlock(blockCreateDto, blockId));
        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void updateBlock_DatesAlreadyBlocked_ThrowsException() {
        var blockId = UUID.randomUUID();
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(propertyService.validUUID(blockId.toString())).thenReturn(blockId);
        Mockito.when(blockRepository.findById(blockId)).thenReturn(Optional.of(new Block(blockId, property, LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now(), null)));
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(property.getId().toString(), blockId.toString(), blockCreateDto.getStartDate(), blockCreateDto.getEndDate())).thenReturn(true);

        var exception = assertThrows(BadRequestException.class, () -> blockService.updateBlock(blockCreateDto, blockId.toString()));
        assertEquals("Dates already blocked", exception.getMessage());
    }

    @Test
    void updateBlock_DatesAlreadyBooked_ThrowsException() {
        var blockId = UUID.randomUUID();
        blockCreateDto.setStartDate(LocalDate.now());
        blockCreateDto.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(propertyService.validUUID(blockId.toString())).thenReturn(blockId);
        Mockito.when(blockRepository.findById(blockId)).thenReturn(Optional.of(new Block(blockId, property, LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now(), null)));
        Mockito.when(blockRepository.existByPropertyIdAndDateRange(property.getId().toString(), blockId.toString(), blockCreateDto.getStartDate(), blockCreateDto.getEndDate())).thenReturn(false);
        Mockito.when(blockService.validateIfDatesAreAvailable(property.getId().toString(), blockId.toString(), blockCreateDto.getStartDate(), blockCreateDto.getEndDate())).thenThrow(new BadRequestException("Dates already booked"));

        var exception = assertThrows(BadRequestException.class, () -> blockService.updateBlock(blockCreateDto, blockId.toString()));
        assertEquals("Dates already booked", exception.getMessage());
    }

}

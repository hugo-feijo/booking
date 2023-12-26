package unit.com.hostfully.interview.service;

import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.repository.PropertyRepository;
import com.hostfully.interview.service.PropertyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

class PropertyServiceTests {

    @Mock
    private PropertyRepository propertyRepository;

    @Spy
    private PropertyCreateDto dto;

    @InjectMocks
    private PropertyService propertyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createProperty_ValidDto_ReturnsEntity() {
        dto.setName("Property Name");
        var entity = dto.toEntity();
        entity.setId(UUID.randomUUID());
        Mockito.when(propertyRepository.save(dto.toEntity())).thenReturn(entity);

        var result = propertyService.createProperty(dto);

        Mockito.verify(propertyRepository, Mockito.times(1)).save(dto.toEntity());
        Mockito.verify(dto, Mockito.times(1)).validate();
        Assertions.assertEquals(entity, result);
    }

    @Test
    public void createProperty_InvalidDto_ThrowsException() {
        dto.setName(null);
        var exception = Assertions.assertThrows(RuntimeException.class, () -> propertyService.createProperty(dto));
        Assertions.assertEquals("Property name is required", exception.getMessage());
        Mockito.verify(dto, Mockito.times(1)).validate();
    }

    @Test
    public void getProperty_ValidId_ReturnsEntity() {
        var uuid = UUID.randomUUID();
        var entity = dto.toEntity();
        entity.setId(uuid);
        Mockito.when(propertyRepository.findById(uuid)).thenReturn(Optional.of(entity));

        var result = propertyService.getProperty(uuid.toString());

        Mockito.verify(propertyRepository, Mockito.times(1)).findById(uuid);
        Assertions.assertEquals(entity, result);
    }

    @Test
    public void getProperty_InvalidId_ThrowsException() {
        var uuid = UUID.randomUUID();
        Mockito.when(propertyRepository.findById(uuid)).thenReturn(Optional.empty());

        var exception = Assertions.assertThrows(RuntimeException.class, () -> propertyService.getProperty(uuid.toString()));

        Mockito.verify(propertyRepository, Mockito.times(1)).findById(uuid);
        Assertions.assertEquals("Bad Request", exception.getMessage());
    }
}

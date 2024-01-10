package com.hostfully.interview.controller.property;

import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.service.BookingService;
import com.hostfully.interview.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyApiController implements PropertyApi {

    private final PropertyService propertyService;
    private final BookingService bookingService;

    public PropertyApiController(PropertyService propertyService, BookingService bookingService) {
        this.propertyService = propertyService;
        this.bookingService = bookingService;
    }

    @Override
    public Property getProperty(@PathVariable("id") String id) {
        return propertyService.getProperty(id);
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyService.findAllProperties();
    }

    @Override
    public ResponseEntity<Property> createProperty(@RequestBody PropertyCreateDto propertyCreateDto) {
        var entity = propertyService.createProperty(propertyCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @Override
    public ResponseEntity<Property> updateProperty(@PathVariable("id") String id, @RequestBody PropertyCreateDto propertyCreateDto) {
        var entity = propertyService.updateProperty(id, propertyCreateDto);
        return ResponseEntity.status(HttpStatus.OK).body(entity);
    }

    @Override
    public ResponseEntity<?> deleteProperty(@PathVariable("id") String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public List<Booking> getBookingsByPropertyId(String propertyId) {
        return bookingService.getBookingsByPropertyId(propertyId);
    }
}

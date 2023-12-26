package com.hostfully.interview.controller;

import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/{id}")
    public Property getProperty(@PathVariable("id") String id) {
        return propertyService.getProperty(id);
    }

    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.findAllProperties();
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody PropertyCreateDto propertyCreateDto) {
        var entity = propertyService.createProperty(propertyCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable("id") String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

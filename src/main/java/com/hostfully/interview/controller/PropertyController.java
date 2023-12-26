package com.hostfully.interview.controller;

import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.service.PropertyService;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public Property createProperty(@RequestBody PropertyCreateDto propertyCreateDto) {
        return propertyService.createProperty(propertyCreateDto);
    }
}

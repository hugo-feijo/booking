package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property createProperty(PropertyCreateDto propertyCreateDto) {
        propertyCreateDto.validate();
        return propertyRepository.save(propertyCreateDto.toEntity());
    }

    public Property getProperty(String id) {
        var uuid = UUID.fromString(id);

        return propertyRepository.findById(uuid)
                .orElseThrow(() -> new BadRequestException("Bad Request"));
    }

    public List<Property> findAllProperties() {
        return propertyRepository.findAll();
    }
}

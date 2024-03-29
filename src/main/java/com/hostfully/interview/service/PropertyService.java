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

    //TODO: extract toEntity method
    public Property createProperty(PropertyCreateDto propertyCreateDto) {
        propertyCreateDto.validate();
        return propertyRepository.save(propertyCreateDto.toEntity());
    }

    public Property getProperty(String id) {
        var uuid = validUUID(id);

        return propertyRepository.findById(uuid)
                .orElseThrow(() -> new BadRequestException("Bad Request"));
    }

    public List<Property> findAllProperties() {
        return propertyRepository.findAll();
    }

    public void deleteProperty(String id) {
        var uuid = validUUID(id);

        propertyRepository.deleteById(uuid);
    }

    //TODO: extract to a util class
    public UUID validUUID(String id) {
        if(id == null) throw new BadRequestException("Bad Request");

        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Bad Request");
        }
    }

    public Property updateProperty(String id, PropertyCreateDto propertyCreateDto) {
        propertyCreateDto.validate();
        var oldProperty = getProperty(id);
        oldProperty.setName(propertyCreateDto.getName());
        return propertyRepository.save(oldProperty);
    }
}

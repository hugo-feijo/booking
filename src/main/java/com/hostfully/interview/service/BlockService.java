package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BlockCreateDto;
import com.hostfully.interview.model.entity.Block;
import com.hostfully.interview.repository.BlockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BlockService {

    private final PropertyService propertyService;
    private final BookingService bookingService;
    private final BlockRepository blockRepository;

    public BlockService(PropertyService propertyService, BookingService bookingService, BlockRepository blockRepository) {
        this.propertyService = propertyService;
        this.bookingService = bookingService;
        this.blockRepository = blockRepository;
    }

    public Block createBlock(BlockCreateDto blockCreateDto, String propertyId) {
        blockCreateDto.validate();
        var property = propertyService.getProperty(propertyId);
        validateIfDatesAreAvailable(propertyId, null, blockCreateDto.getStartDate(), blockCreateDto.getEndDate());
        var block = new Block(null, property, blockCreateDto.getStartDate(), blockCreateDto.getEndDate());
        return blockRepository.save(block);
    }

    public boolean validateIfDatesAreAvailable(String propertyId, String blockId, LocalDate startDate, LocalDate endDate) {
        var isBooked = blockRepository.existByPropertyIdAndDateRange(propertyId, blockId, startDate, endDate);

        if(isBooked) {
            throw new BadRequestException("Dates already blocked");
        }

        bookingService.validateIfDatesAreAvailable(propertyId, null, startDate, endDate);

        return true;
    }
}

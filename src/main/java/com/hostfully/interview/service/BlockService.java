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
        var block = new Block(null, property, blockCreateDto.getStartDate(), blockCreateDto.getEndDate(), LocalDate.now(), null);
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

    public Block updateBlock(BlockCreateDto blockCreateDto, String blockId) {
        blockCreateDto.validate();
        var block = getBlock(blockId);
        validateIfDatesAreAvailable(block.getProperty().getId().toString(), blockId, blockCreateDto.getStartDate(), blockCreateDto.getEndDate());
        block.setStartDate(blockCreateDto.getStartDate());
        block.setEndDate(blockCreateDto.getEndDate());
        block.setUpdateAt(LocalDate.now());
        return blockRepository.save(block);
    }

    public Block getBlock(String blockId) {
        var UUID = propertyService.validUUID(blockId);
        return blockRepository.findById(UUID).orElseThrow(() -> new BadRequestException("Bad Request"));
    }
}

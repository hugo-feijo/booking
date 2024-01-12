package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.repository.BlockRepository;
import com.hostfully.interview.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {
    private final BlockRepository blockRepository;
    private final BookingRepository bookingRepository;

    public ReservationService(BlockRepository blockRepository, BookingRepository bookingRepository) {
        this.blockRepository = blockRepository;
        this.bookingRepository = bookingRepository;
    }

    public boolean isDatesBooked(String propertyId, String bookingId, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.existByPropertyIdAndDateRange(propertyId, bookingId, startDate, endDate);
    }

    public boolean isDatesBlocked(String propertyId, String blockId, LocalDate startDate, LocalDate endDate) {
        return blockRepository.existByPropertyIdAndDateRange(propertyId, blockId, startDate, endDate);
    }

    public boolean validateIfDatesAreAvailable(String propertyId, LocalDate startDate, LocalDate endDate) {
        return validateIfDatesAreAvailable(propertyId, null, null, startDate, endDate);
    }

    public boolean validateIfDatesAreAvailable(String propertyId, String bookingId, String blockId, LocalDate startDate, LocalDate endDate) {
        if (isDatesBooked(propertyId, bookingId, startDate, endDate))
            throw new BadRequestException("Dates are already booked");

        if (isDatesBlocked(propertyId, blockId, startDate, endDate))
            throw new BadRequestException("Dates are already blocked");

        return true;
    }
}

package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final PropertyService propertyService;
    private final BookingRepository bookingRepository;

    public BookingService(PropertyService propertyService, BookingRepository bookingRepository) {
        this.propertyService = propertyService;
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(BookingCreateDto bookingCreateDto) {
        bookingCreateDto.validate();
        var booking = bookingCreateDtoToBooking(bookingCreateDto);
        validateBookingDates(bookingCreateDto);
        return bookingRepository.save(booking);
    }

    public boolean validateBookingDates(BookingCreateDto bookingCreateDto) {
        var isBooked = bookingRepository.existByPropertyIdAndDateRange(bookingCreateDto.getPropertyId(), bookingCreateDto.getStartDate(), bookingCreateDto.getEndDate());

        if(isBooked) {
            throw new BadRequestException("Dates already booked");
        }

        return true;
    }

    public Booking bookingCreateDtoToBooking(BookingCreateDto bookingCreateDto) {
        var booking = new Booking();

        var property = propertyService.getProperty(bookingCreateDto.getPropertyId());
        booking.setProperty(property);
        booking.setStartDate(bookingCreateDto.getStartDate());
        booking.setEndDate(bookingCreateDto.getEndDate());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDate.now());

        return booking;
    }

    public List<Booking> getBookingsByPropertyId(String propertyId) {
        var property = propertyService.getProperty(propertyId);
        return bookingRepository.findAllByProperty(property);
    }
}

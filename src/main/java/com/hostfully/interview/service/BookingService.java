package com.hostfully.interview.service;

import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
        return bookingRepository.save(booking);
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
}

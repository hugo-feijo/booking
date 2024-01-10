package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import org.springframework.stereotype.Service;

@Service
public class GuestService {

    private final BookingService bookingService;

    public GuestService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Booking createGuest(String bookingId, GuestCreateDTO guestCreateDTO) {
        var booking = bookingService.getBooking(bookingId);
        validateGuest(guestCreateDTO);
        booking.getGuests().add(guestCreateDTO.toEntity());
        return bookingService.saveBooking(booking);
    }

    private boolean validateGuest(GuestCreateDTO guestCreateDTO) {
        if(guestCreateDTO == null) {
            throw new BadRequestException("Guest is required");
        }
        guestCreateDTO.validate();
        return true;
    }
}

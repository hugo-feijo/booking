package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Guest;
import com.hostfully.interview.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GuestService {

    private final BookingService bookingService;
    private final GuestRepository guestRepository;

    public GuestService(BookingService bookingService, GuestRepository guestRepository) {
        this.bookingService = bookingService;
        this.guestRepository = guestRepository;
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

    public Guest getGuest(String guestId) {
        UUID uuid = null;
        if (guestId == null) {
            throw new BadRequestException("Guest id is required");
        }

        try {
            uuid = UUID.fromString(guestId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid guest id");
        }


        return guestRepository.findById(uuid).orElseThrow(() -> new BadRequestException("Bad Request"));
    }

    public Guest updateGuest(String guestId, GuestCreateDTO guestCreateDTO) {
        var guest = getGuest(guestId);
        guest.setName(guestCreateDTO.getName());
        return guestRepository.save(guest);
    }

    public void deleteGuest(String bookingId, String guestId) {
        var guest = getGuest(guestId);
        var booking = bookingService.getBooking(bookingId);
        validateBookingGuest(booking);

        guestRepository.delete(guest);
    }

    private boolean validateBookingGuest(Booking booking) {
        if(booking.getGuests().size() == 1) {
            throw new BadRequestException("Booking must have at least one guest");
        }
        return true;
    }
}

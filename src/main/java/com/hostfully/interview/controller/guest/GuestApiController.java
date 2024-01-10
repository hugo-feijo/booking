package com.hostfully.interview.controller.guest;

import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Guest;
import com.hostfully.interview.service.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
public class GuestApiController implements GuestApi {

    private final GuestService guestService;

    public GuestApiController(GuestService guestService) {
        this.guestService = guestService;
    }

    @Override
    public ResponseEntity<Booking> createGuest(String bookingId, GuestCreateDTO guestCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.createGuest(bookingId, guestCreateDTO));
    }

    @Override
    public ResponseEntity<Guest> updateGuest(String guestId, GuestCreateDTO guestCreateDTO) {
        return ResponseEntity.ok(guestService.updateGuest(guestId, guestCreateDTO));
    }
}

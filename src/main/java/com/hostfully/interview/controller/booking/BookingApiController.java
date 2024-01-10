package com.hostfully.interview.controller.booking;

import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
public class BookingApiController implements BookingApi {

    private final BookingService bookingService;

    public BookingApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public ResponseEntity<Booking> createProperty(BookingCreateDto bookingCreateDto) {
        var entity = bookingService.createBooking(bookingCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @Override
    public ResponseEntity<Booking> cancelBooking(String bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

}

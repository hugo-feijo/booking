package com.hostfully.interview.service;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.BookingUpdateDto;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final PropertyService propertyService;
    private final ReservationService reservationService;
    private final BookingRepository bookingRepository;

    public BookingService(PropertyService propertyService, ReservationService reservationService, BookingRepository bookingRepository) {
        this.propertyService = propertyService;
        this.reservationService = reservationService;
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(BookingCreateDto bookingCreateDto) {
        bookingCreateDto.validate();
        var booking = bookingCreateDtoToBooking(bookingCreateDto);
        reservationService.validateIfDatesAreAvailable(bookingCreateDto.getPropertyId(), bookingCreateDto.getStartDate(), bookingCreateDto.getEndDate());
        return saveBooking(booking);
    }

    public Booking bookingCreateDtoToBooking(BookingCreateDto bookingCreateDto) {
        var booking = new Booking();

        var property = propertyService.getProperty(bookingCreateDto.getPropertyId());
        booking.setProperty(property);
        booking.setStartDate(bookingCreateDto.getStartDate());
        booking.setEndDate(bookingCreateDto.getEndDate());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDate.now());
        booking.setGuests(bookingCreateDto.getGuests().stream().map(GuestCreateDTO::toEntity).toList());

        return booking;
    }

    public Booking getBooking(String bookingId) {
        var uuid = propertyService.validUUID(bookingId);
        return bookingRepository.findById(uuid).orElseThrow(() -> new BadRequestException("Bad Request"));
    }

    public List<Booking> getBookingsByPropertyId(String propertyId) {
        var property = propertyService.getProperty(propertyId);
        return bookingRepository.findAllByProperty(property);
    }

    public Booking cancelBooking(String bookingId) {
        var booking = getBooking(bookingId);
        validateBookingForCancellation(booking);

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdateAt(LocalDate.now());

        return saveBooking(booking);
    }

    private boolean validateBookingForCancellation(Booking booking) {
        if(booking.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new BadRequestException("Booking already cancelled");
        }
        return true;
    }

    public Booking rebookBooking(String bookingId) {
        var booking = getBooking(bookingId);
        validateBookingForRebooking(booking);
        reservationService.validateIfDatesAreAvailable(booking.getProperty().getId().toString(), booking.getId().toString(), null, booking.getStartDate(), booking.getEndDate());

        booking.setStatus(BookingStatus.CONFIRMED); //TODO: space to improve, make host able to approve rebooking
        booking.setUpdateAt(LocalDate.now());

        return saveBooking(booking);
    }

    private boolean validateBookingForRebooking(Booking booking) {
        if(!booking.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new BadRequestException("Booking not cancelled");
        }
        return true;
    }

    public void deleteBooking(String bookingId) {
        var booking = getBooking(bookingId);
        bookingRepository.delete(booking);
    }

    public Booking updateBooking(String bookingId, BookingUpdateDto bookingUpdateDto) {
        bookingUpdateDto.validate();
        var booking = getBooking(bookingId);
        reservationService.validateIfDatesAreAvailable(booking.getProperty().getId().toString(), booking.getId().toString(), null, bookingUpdateDto.getStartDate(), bookingUpdateDto.getEndDate());

        booking.setStartDate(bookingUpdateDto.getStartDate());
        booking.setEndDate(bookingUpdateDto.getEndDate());

        return saveBooking(booking);
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
}

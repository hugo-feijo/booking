package com.hostfully.interview.controller.booking;

import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.BookingUpdateDto;
import com.hostfully.interview.model.dto.ErrorInfoDto;
import com.hostfully.interview.model.entity.Booking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "booking", description = "the Booking API")
public interface BookingApi {
    @Operation(summary = "Create Booking", description = "Create a new Booking, if dates are not blocked or booked", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PostMapping
    ResponseEntity<Booking> createProperty(@RequestBody BookingCreateDto bookingCreateDto);

    @Operation(summary = "Cancel a Booking", description = "Cancel a Booking", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PutMapping("/{id}/action/cancel")
    ResponseEntity<Booking> cancelBooking(@Parameter(description="Booking id") @PathVariable("id") String bookingId);

    @Operation(summary = "Rebook a Booking", description = "Rebook a cancelled Booking, if dates are not blocked or booked", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PutMapping("/{id}/action/rebook")
    ResponseEntity<Booking> rebookBooking(@Parameter(description="Booking id") @PathVariable("id") String bookingId);

    @Operation(summary = "Get a Booking", description = "Get a Booking", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @GetMapping("/{id}")
    ResponseEntity<Booking> getBooking(@Parameter(description="Booking id") @PathVariable("id") String bookingId);

    @Operation(summary = "Delete a Booking", description = "Delete a Booking", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteBooking(@Parameter(description="Booking id")  @PathVariable("id") String bookingId);

    @Operation(summary = "Update a Booking", description = "Update a Booking", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PutMapping("/{id}")
    ResponseEntity<Booking> updateBooking(@Parameter(description="Booking id") @PathVariable("id") String bookingId, @RequestBody BookingUpdateDto bookingUpdateDto);
}

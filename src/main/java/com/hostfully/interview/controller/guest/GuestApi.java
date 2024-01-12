package com.hostfully.interview.controller.guest;

import com.hostfully.interview.model.dto.ErrorInfoDto;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Guest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "guest", description = "the Guest API")
public interface GuestApi {

    @Operation(summary = "Create a Guest", description = "Create a new Guest", tags = {"guest"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PostMapping("/{id}/guest")
    ResponseEntity<Booking> createGuest(@Parameter(description="Booking id") @PathVariable("id") String bookingId, @RequestBody GuestCreateDTO guestCreateDTO);

    @Operation(summary = "Update a Guest", description = "Update a Guest Details", tags = {"guest"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PostMapping("/guest/{id}")
    ResponseEntity<Guest> updateGuest(@Parameter(description="Guest id") @PathVariable("id") String guestId, @RequestBody GuestCreateDTO guestCreateDTO);

    @Operation(summary = "Deleting Guest", description = "Deleting a Guest", tags = {"guest"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @DeleteMapping("/{booking-id}/guest/{guest-id}")
    ResponseEntity<?> deleteGuest(@Parameter(description="Booking id") @PathVariable("booking-id") String bookingId, @Parameter(description="Guest id") @PathVariable("guest-id") String guestId);
}

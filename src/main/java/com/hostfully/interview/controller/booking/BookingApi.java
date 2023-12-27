package com.hostfully.interview.controller.booking;

import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.ErrorInfoDto;
import com.hostfully.interview.model.entity.Booking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "booking", description = "the Booking API")
public interface BookingApi {
    @Operation(summary = "Create Booking", description = "Create a new Booking", tags = {"booking"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PostMapping
    ResponseEntity<Booking> createProperty(@RequestBody BookingCreateDto bookingCreateDto);
}

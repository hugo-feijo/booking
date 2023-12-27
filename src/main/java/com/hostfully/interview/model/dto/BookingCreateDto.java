package com.hostfully.interview.model.dto;


import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingCreateDto {

    @Schema(description = "Property ID (UUID) that this booking belongs", example = "c4eada6d-dbba-4be2-ad3b-92995154a682", requiredMode = Schema.RequiredMode.REQUIRED)
    private String propertyId;

    @Schema(description = "When the booking start", example = "New Unit", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @Schema(description = "When the booking end", example = "New Unit", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    public boolean validate() {
        return validatePropertyId() &&
                validateDates();
    }

    public boolean validatePropertyId() {
        var valid = propertyId != null && !propertyId.isEmpty();

        try {
            UUID.fromString(propertyId);
        } catch (Exception e) {
            valid = false;
        }

        if(!valid) {
            throw new BadRequestException("Bad Request");
        }

        return valid;
    }

    public Booking toBooking(Property property) {
        var booking = new Booking();
        booking.setProperty(property);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        return booking;
    }

    public boolean validateDates() {
        var valid = startDate != null && endDate != null && startDate.isBefore(endDate);

        if(!valid) {
            throw new BadRequestException("Bad Request");
        }

        return valid;
    }
}

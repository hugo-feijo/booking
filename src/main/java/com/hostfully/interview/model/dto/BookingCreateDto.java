package com.hostfully.interview.model.dto;


import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
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

    @Schema(description = "Guests that belongs to this booking", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<GuestCreateDTO> guests;

    public boolean validate() {
        return validatePropertyId() &&
                validateDates() &&
                validateGuests();
    }

    public boolean validatePropertyId() {
        var valid = propertyId != null && !propertyId.isEmpty();

        try {
            UUID.fromString(propertyId);
        } catch (Exception e) {
            valid = false;
        }

        if(!valid) {
            throw new BadRequestException("Property ID is required and must be a valid UUID");
        }

        return valid;
    }

    public boolean validateDates() {
        var valid = startDate != null && endDate != null && startDate.isBefore(endDate);
        var errorMessage = "Bad Request";

        if(startDate == null) errorMessage = "Start date is required";
        if(endDate == null) errorMessage = "End date is required";
        if(startDate != null && endDate != null && startDate.isAfter(endDate)) errorMessage = "Start date must be before end date";

        if(!valid) {
            throw new BadRequestException(errorMessage);
        }

        return valid;
    }

    public boolean validateGuests() {
        var valid = guests != null && !guests.isEmpty();
        var errorMessage = "Guests is required";

        if(!valid) {
            throw new BadRequestException(errorMessage);
        }

        guests.forEach(GuestCreateDTO::validate);

        return valid;
    }
}

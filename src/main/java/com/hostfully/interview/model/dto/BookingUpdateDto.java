package com.hostfully.interview.model.dto;


import com.hostfully.interview.exception.BadRequestException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingUpdateDto {

    @Schema(description = "When the booking start", example = "New Unit", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @Schema(description = "When the booking end", example = "New Unit", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    public boolean validate() {
        return validateDates();
    }

    //TODO: extract to a common class
    public boolean validateDates() {
        var valid = startDate != null && endDate != null && startDate.isBefore(endDate);
        var errorMessage = "Bad Request";

        if(startDate == null) errorMessage = "Start date is required";
        if(endDate == null) errorMessage = "End date is required";
        if(startDate != null && endDate != null && startDate.isAfter(endDate)) errorMessage = "Start date must be before end date";
        if(startDate != null && endDate != null && startDate.isEqual(endDate)) errorMessage = "Start date must be different than end date";

        if(!valid) {
            throw new BadRequestException(errorMessage);
        }

        return valid;
    }
}

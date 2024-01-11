package com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.entity.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BlockCreateDto {
    
    @Schema(description = "When the booking start", example = "2021-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate startDate;

    @Schema(description = "When the booking end", example = "2022-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate endDate;

    public boolean validate() {
        return validateDates();
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
}

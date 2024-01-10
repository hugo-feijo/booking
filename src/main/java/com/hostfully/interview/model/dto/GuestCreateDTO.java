package com.hostfully.interview.model.dto;


import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.entity.Guest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GuestCreateDTO {

    @Schema(description = "Guest Name", example = "Alan Wake", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    public Guest toEntity() {
        return new Guest(null, name, LocalDate.now(), null);
    }

    public boolean validate() {
        return validateName();
    }
    public boolean validateName() {
        if(name == null || name.isEmpty()) {
            throw new BadRequestException("Guest name is required");
        }

        return true;
    }
}

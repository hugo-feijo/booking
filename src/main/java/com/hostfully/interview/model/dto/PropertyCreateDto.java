package com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.entity.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyCreateDto {
    @Schema(description = "Property name.", example = "New unit",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    public Property toEntity() {
        return new Property(null, name);
    }

    public boolean validate() {
        return validateName();
    }

    public boolean validateName() {
        if (name == null || name.isEmpty()){
            throw new BadRequestException("Property name is required");
        }
        return true;
    }
}

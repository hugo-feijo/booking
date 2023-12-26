package com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.DtoNotValidException;
import com.hostfully.interview.model.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyCreateDto {
    private String name;

    public Property toEntity() {
        return new Property(null, name);
    }

    public boolean validate() {
        return validateName();
    }

    public boolean validateName() {
        if (name == null || name.isEmpty()){
            throw new DtoNotValidException("Property name is required");
        }
        return true;
    }
}

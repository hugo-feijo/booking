package com.hostfully.interview.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorInfoDto {
    @Schema(description = "Error message.", example = "Property Name is required",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
    public ErrorInfoDto(Exception ex) {
        this.message = ex.getMessage();
    }

    public ErrorInfoDto(String message) {
        this.message = message;
    }
}

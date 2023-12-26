package com.hostfully.interview.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorInfoDto {
    private String message;
    public ErrorInfoDto(Exception ex) {
        this.message = ex.getMessage();
    }

    public ErrorInfoDto(String message) {
        this.message = message;
    }
}

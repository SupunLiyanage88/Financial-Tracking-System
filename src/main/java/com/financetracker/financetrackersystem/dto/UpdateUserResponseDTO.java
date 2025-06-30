package com.financetracker.financetrackersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUserResponseDTO {

    private String message;
    private String error;
}

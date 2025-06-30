package com.financetracker.financetrackersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DeleteUserResponseDTO {

    private String message;
    private String error;

}

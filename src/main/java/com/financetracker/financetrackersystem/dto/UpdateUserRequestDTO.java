package com.financetracker.financetrackersystem.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateUserRequestDTO {

    private String email;
    private String password;
    private String username;
    private String name;
    private String role;

}

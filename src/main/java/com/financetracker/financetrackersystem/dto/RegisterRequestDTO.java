package com.financetracker.financetrackersystem.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequestDTO {
        
        private String id;
        private String name;
        private String email;
        private String username;
        private String password;
        private String role;

}

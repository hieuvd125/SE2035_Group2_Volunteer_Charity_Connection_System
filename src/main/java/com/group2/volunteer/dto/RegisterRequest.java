package com.group2.volunteer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class RegisterRequest {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String role;
}

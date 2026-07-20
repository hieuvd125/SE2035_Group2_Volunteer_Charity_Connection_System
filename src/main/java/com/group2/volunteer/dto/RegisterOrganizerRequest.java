package com.group2.volunteer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterOrganizerRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String city;
    private String address;

    private String website;
    private String description;
}

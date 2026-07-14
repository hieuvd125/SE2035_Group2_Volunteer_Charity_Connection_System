package com.group2.volunteer.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginDTO {
    private String email;
    private String password;
}

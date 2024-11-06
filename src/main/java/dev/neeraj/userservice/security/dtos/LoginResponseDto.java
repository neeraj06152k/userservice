package dev.neeraj.userservice.security.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {
    private String email;
    private String firstname;
    private String lastname;
    private List<String> roles;
}


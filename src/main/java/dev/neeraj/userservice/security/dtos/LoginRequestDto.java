package dev.neeraj.userservice.security.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginRequestDto {
    private String email;
    @ToString.Exclude
    private String password;
}


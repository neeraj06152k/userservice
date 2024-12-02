package dev.neeraj.userservice.security.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SignupRequestDto {
    private String email;
    private String firstname;
    private String lastname;
    @ToString.Exclude
    private String password;
}

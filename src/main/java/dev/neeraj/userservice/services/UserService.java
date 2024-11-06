package dev.neeraj.userservice.services;

import dev.neeraj.userservice.exceptions.UserAlreadyExists;
import dev.neeraj.userservice.exceptions.UserNotFound;
import dev.neeraj.userservice.models.User;
import dev.neeraj.userservice.security.dtos.SignupRequestDto;

public interface UserService{

    User getUserById(long id) throws UserNotFound;

    boolean existsByEmail(String email);

    User signup(SignupRequestDto signupDto) throws UserAlreadyExists;
}

package dev.neeraj.userservice.services;

import dev.neeraj.userservice.exceptions.UserAlreadyExists;
import dev.neeraj.userservice.exceptions.UserNotFound;
import dev.neeraj.userservice.models.Role;
import dev.neeraj.userservice.models.User;
import dev.neeraj.userservice.repositories.UserRepository;
import dev.neeraj.userservice.security.dtos.SignupRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public User getUserById(long id) throws UserNotFound {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User with ID: " + id + " not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public User signup(SignupRequestDto signupDto) throws UserAlreadyExists {

        if(this.existsByEmail(signupDto.getEmail()))
            throw new UserAlreadyExists("UserAlreadyExists");

        Role userRole = new Role();
        userRole.setName("USER");

        User user = new User();
        user.setEmail(signupDto.getEmail());
        user.setFirstname(signupDto.getFirstname());
        user.setLastname(signupDto.getLastname());
        user.setPassword(signupDto.getPassword());
        user.setRoles(List.of(userRole));

        return userRepository.save(user);
    }
}

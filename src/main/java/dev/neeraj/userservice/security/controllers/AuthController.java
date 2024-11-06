package dev.neeraj.userservice.security.controllers;

import dev.neeraj.userservice.exceptions.*;
import dev.neeraj.userservice.security.dtos.LoginRequestDto;
import dev.neeraj.userservice.security.dtos.LoginResponseDto;
import dev.neeraj.userservice.security.dtos.SignupRequestDto;
import dev.neeraj.userservice.models.Role;
import dev.neeraj.userservice.models.User;
import dev.neeraj.userservice.security.jwt.JwtUtils;
import dev.neeraj.userservice.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Environment env;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto)
            throws UsernameNotFoundException, InvalidCredentialsException {

        UserDetails userDetails = userDetailsService.loadUserByUsername(requestDto.getEmail());

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                requestDto.getPassword()
        );

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }


        ResponseCookie responseCookie = jwtUtils.generateJwtCookie(userDetails);

        LoginResponseDto responseDto = toLoginResponseDto(User.toUser(userDetails));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(responseDto);
    }
    private LoginResponseDto toLoginResponseDto(User user) {
        LoginResponseDto responseDto = new LoginResponseDto();

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        responseDto.setEmail(user.getEmail());
        responseDto.setFirstname(user.getFirstname());
        responseDto.setLastname(user.getLastname());
        responseDto.setRoles(roles);

        return responseDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie responseCookie = jwtUtils.generateCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body("Logged out successfully");
    }


    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDto> signup(@RequestParam SignupRequestDto requestDto)
            throws UserAlreadyExists, InvalidSignupRequest, InvalidUser {

        if(!isSignupRequestValid(requestDto))
            throw new InvalidSignupRequest("InvalidSignupRequest");

        User newUser = userService.signup(requestDto);

        return ResponseEntity.ok().body(toLoginResponseDto(newUser));
    }


    @GetMapping("/jwt")
    public ResponseEntity<Map<String, String>> testJwt(HttpServletRequest request) {
        String cookieName = env.getProperty("spring.app.jwtCookieName");
        Map<String, String> response = new HashMap<>();
        String token = "";

        Cookie[] cookies = request.getCookies();

        if(cookies!=null && cookies.length>0) {
            Optional<Cookie> optionalCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(cookieName))
                    .findFirst();

            if (optionalCookie.isPresent()) token = optionalCookie.get().getValue();
        }


        response.put("jwt", token);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String getUsername(Authentication authentication) {
        if(authentication==null) return "NULL";

        String name = authentication.getName();
        return name;
    }


    private boolean isSignupRequestValid(SignupRequestDto requestDto) {
        return requestDto.getEmail() != null
                && requestDto.getFirstname() != null
                && requestDto.getLastname() != null
                && requestDto.getPassword() != null;
    }
}

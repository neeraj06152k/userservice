package dev.neeraj.userservice.security.configurations;

import ch.qos.logback.core.encoder.Encoder;
import dev.neeraj.userservice.models.Role;
import dev.neeraj.userservice.models.User;
import dev.neeraj.userservice.repositories.RoleRepository;
import dev.neeraj.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.loginPage("/api/v1/login"))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


//        http.authorizeHttpRequests()
//                .requestMatchers("/ public/**").permitAll().anyRequest()
//                .hasRole("USER").and()
//                // Possibly more configuration ...
//                .formLogin() // enable form based log in
//                // set permitAll for all URLs associated with Form Login
//                .permitAll();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration,
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public CommandLineRunner commandLineRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String userRoleName = "ROLE_USER";
            String adminRoleName = "ROLE_ADMIN";

            Role userRole = roleRepository.findByNameIgnoreCase(userRoleName)
                    .orElseGet(() -> roleRepository.save(new Role(userRoleName)));
            Role adminRole = roleRepository.findByNameIgnoreCase(adminRoleName)
                    .orElseGet(() -> roleRepository.save(new Role(adminRoleName)));


            User user1 = new User();
            user1.setEmail("admin@dev");
            user1.setPassword(passwordEncoder.encode("admin"));
            user1.setRoles(List.of(userRole, adminRole));
            user1.setFirstname("Admin");
            user1.setLastname("Admin");

            User saved = userRepository.save(user1);

            System.out.println(saved.getEmail()+" "+saved.getPassword()+";");


//            User user2 = new User();
//            user2.setEmail("admin@dev");
//            user2.setPassword(passwordEncoder.encode("admin"));
//            user2.setRoles(List.of(userRole, adminRole));
//            user2.setFirstname("Admin");
//            user2.setLastname("Admin");
//
//            userRepository.save(user2);
        };
    }

}

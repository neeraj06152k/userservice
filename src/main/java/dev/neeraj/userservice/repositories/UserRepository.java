package dev.neeraj.userservice.repositories;

import dev.neeraj.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmail(String email);
}

package com.courtreservation.authentication.repository;

import com.courtreservation.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserMail(String userMail);

    boolean existsByUserMail(String userMail);
}

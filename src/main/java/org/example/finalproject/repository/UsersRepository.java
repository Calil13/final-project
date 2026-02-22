package org.example.finalproject.repository;

import org.example.finalproject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmailAndDeletedFalse(String email);
    //Optional<Users> findByEmailAndDeletedFalse(String email);
    boolean existsByEmail(String email);
}

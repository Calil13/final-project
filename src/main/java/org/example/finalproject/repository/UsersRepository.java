package org.example.finalproject.repository;

import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndPasswordAndPhoneAndNameAndSurname(
            String email,
            String password,
            String phone,
            String name,
            String surname);
}

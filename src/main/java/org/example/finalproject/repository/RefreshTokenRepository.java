package org.example.finalproject.repository;

import org.example.finalproject.entity.RefreshToken;
import org.example.finalproject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(Users user);

    void deleteByToken(String token);

    Optional<RefreshToken> findByUser(Users user);
}

package org.example.finalproject.repository;

import org.example.finalproject.entity.Users;
import org.example.finalproject.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUser(Users user);
}

package org.example.finalproject.repository;

import org.example.finalproject.entity.Users;
import org.example.finalproject.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUser(Users user);
}

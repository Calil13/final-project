package org.example.finalproject.repository;

import jakarta.persistence.Id;
import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.entity.Address;
import org.example.finalproject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUser(Users user);
}



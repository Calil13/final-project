package org.example.finalproject.repository;

import org.example.finalproject.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorites, Long> {
}

package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.FavoriteProductsDto;
import org.example.finalproject.service.FavoriteService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/favorites")
@Tag(name = "Favorites API", description = "Endpoints for managing user's favorite items")
public class FavoritesController {

    private final FavoriteService favoriteService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Page<FavoriteProductsDto> getFavorites(@ParameterObject Pageable pageable) {
        return favoriteService.getFavorites(pageable);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{productId}")
    public void addFavorite(@PathVariable Long productId) {
        favoriteService.addFavorite(productId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("{favoriteId}")
    public void deleteFavorite(@PathVariable Long favoriteId) {
        favoriteService.deleteFavorite(favoriteId);
    }
}

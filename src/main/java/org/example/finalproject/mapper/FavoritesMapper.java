package org.example.finalproject.mapper;

import org.example.finalproject.dto.FavoriteProductsDto;
import org.example.finalproject.entity.Favorites;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoritesMapper {

    FavoriteProductsDto toDto(Favorites favorites);
}

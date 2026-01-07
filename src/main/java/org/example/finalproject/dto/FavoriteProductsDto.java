package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finalproject.entity.Products;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProductsDto {
    private ProductResponseDto product;
}

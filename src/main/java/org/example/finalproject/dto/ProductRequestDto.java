package org.example.finalproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRequestDto {
    private Long productId;
    private Long ownerId;
    private Long categoryId;
    private String name;
    private String description;
    private Double price;
}

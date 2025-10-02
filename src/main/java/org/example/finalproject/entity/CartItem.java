package org.example.finalproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;

    private Integer quantity;
}

package ua.com.owu.productservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(
        String id,
        String name,
        String category,
        BigDecimal price,
        String ownerEmail
) {
}

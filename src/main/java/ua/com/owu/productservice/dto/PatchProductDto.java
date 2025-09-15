package ua.com.owu.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import ua.com.owu.productservice.validation.constraints.NotBlankIfPresent;

import java.math.BigDecimal;

@Builder
public record PatchProductDto(
        @NotBlankIfPresent String name,
        @NotBlankIfPresent String category,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotBlank String ownerEmail
) {
}

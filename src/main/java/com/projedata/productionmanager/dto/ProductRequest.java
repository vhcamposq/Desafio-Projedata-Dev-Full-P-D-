package com.projedata.productionmanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull List<ProductIngredientRequest> ingredients
) {}

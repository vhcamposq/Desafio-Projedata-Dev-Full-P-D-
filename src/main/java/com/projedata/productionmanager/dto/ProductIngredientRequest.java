package com.projedata.productionmanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductIngredientRequest(
        @NotNull Long rawMaterialId,
        @NotNull @DecimalMin("0.01") BigDecimal quantityRequired
) {}

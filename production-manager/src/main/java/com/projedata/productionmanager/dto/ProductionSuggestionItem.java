package com.projedata.productionmanager.dto;

import java.math.BigDecimal;

public record ProductionSuggestionItem(
        Long productId,
        String productCode,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalValue
) {}

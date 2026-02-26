package com.projedata.productionmanager.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductionSuggestionResponse(
        List<ProductionSuggestionItem> items,
        BigDecimal grandTotal
) {}

package com.projedata.productionmanager.service;

import com.projedata.productionmanager.dto.ProductionSuggestionItem;
import com.projedata.productionmanager.dto.ProductionSuggestionResponse;
import com.projedata.productionmanager.entity.Product;
import com.projedata.productionmanager.entity.ProductIngredient;
import com.projedata.productionmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductionCalculatorService {

    private final ProductRepository productRepository;

    public ProductionSuggestionResponse calculate() {
        List<Product> products = productRepository.findAllWithIngredients();

        // Sort products by price descending (greedy: highest value first)
        products.sort(Comparator.comparing(Product::getPrice).reversed());

        // Build a mutable stock map: rawMaterialId -> available quantity
        Map<Long, BigDecimal> availableStock = buildStockMap(products);

        List<ProductionSuggestionItem> items = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Product product : products) {
            if (product.getIngredients().isEmpty()) {
                continue;
            }

            int feasibleUnits = calculateFeasibleUnits(product, availableStock);

            if (feasibleUnits <= 0) {
                continue;
            }

            // Consume stock
            consumeStock(product, feasibleUnits, availableStock);

            BigDecimal totalValue = product.getPrice().multiply(BigDecimal.valueOf(feasibleUnits));
            grandTotal = grandTotal.add(totalValue);

            items.add(new ProductionSuggestionItem(
                    product.getId(),
                    product.getCode(),
                    product.getName(),
                    feasibleUnits,
                    product.getPrice(),
                    totalValue
            ));
        }

        return new ProductionSuggestionResponse(items, grandTotal);
    }

    private Map<Long, BigDecimal> buildStockMap(List<Product> products) {
        Map<Long, BigDecimal> stock = new HashMap<>();
        for (Product product : products) {
            for (ProductIngredient ingredient : product.getIngredients()) {
                Long rawMaterialId = ingredient.getRawMaterial().getId();
                stock.putIfAbsent(rawMaterialId, ingredient.getRawMaterial().getStockQuantity());
            }
        }
        return stock;
    }

    private int calculateFeasibleUnits(Product product, Map<Long, BigDecimal> availableStock) {
        int feasible = Integer.MAX_VALUE;
        for (ProductIngredient ingredient : product.getIngredients()) {
            Long rawMaterialId = ingredient.getRawMaterial().getId();
            BigDecimal available = availableStock.getOrDefault(rawMaterialId, BigDecimal.ZERO);
            BigDecimal required = ingredient.getQuantityRequired();

            if (required.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            int possible = available.divideToIntegralValue(required).intValue();
            feasible = Math.min(feasible, possible);
        }
        return feasible == Integer.MAX_VALUE ? 0 : feasible;
    }

    private void consumeStock(Product product, int units, Map<Long, BigDecimal> availableStock) {
        for (ProductIngredient ingredient : product.getIngredients()) {
            Long rawMaterialId = ingredient.getRawMaterial().getId();
            BigDecimal consumed = ingredient.getQuantityRequired().multiply(BigDecimal.valueOf(units));
            availableStock.merge(rawMaterialId, consumed, BigDecimal::subtract);
        }
    }
}

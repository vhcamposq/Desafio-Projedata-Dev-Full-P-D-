package com.projedata.productionmanager.service;

import com.projedata.productionmanager.dto.ProductionSuggestionResponse;
import com.projedata.productionmanager.entity.Product;
import com.projedata.productionmanager.entity.ProductIngredient;
import com.projedata.productionmanager.entity.RawMaterial;
import com.projedata.productionmanager.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductionCalculatorServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductionCalculatorService service;

    private RawMaterial flour;
    private RawMaterial sugar;

    @BeforeEach
    void setUp() {
        flour = buildRawMaterial(1L, "RM-001", "Flour", new BigDecimal("1000"));
        sugar = buildRawMaterial(2L, "RM-002", "Sugar", new BigDecimal("500"));
    }

    @Test
    @DisplayName("Should suggest production when stock is sufficient for all products")
    void shouldSuggestProductionWhenStockIsSufficient() {
        // Produto A: precisa de 100g de farinha, vende por 50,00
        // Produto B: precisa de 200g de farinha, vende por 30,00
        // Estoque: 1000g de farinha -> 10 unidades de A (B vem depois com sobra)
        Product productA = buildProduct(1L, "P-001", "Cake", new BigDecimal("50.00"));
        addIngredient(productA, flour, new BigDecimal("100"));

        Product productB = buildProduct(2L, "P-002", "Cookie", new BigDecimal("30.00"));
        addIngredient(productB, flour, new BigDecimal("200"));

        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>(List.of(productA, productB)));

        ProductionSuggestionResponse response = service.calculate();

        // Greedy: A primeiro (preço 50 > 30), 1000/100 = 10 unidades de A
        // Farinha restante: 0g -> 0 unidades de B
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productCode()).isEqualTo("P-001");
        assertThat(response.items().get(0).quantity()).isEqualTo(10);
        assertThat(response.grandTotal()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Should return empty suggestion when stock is insufficient for any product")
    void shouldReturnEmptyWhenStockIsInsufficient() {
        RawMaterial emptyStock = buildRawMaterial(3L, "RM-003", "Yeast", new BigDecimal("0"));

        Product product = buildProduct(1L, "P-001", "Bread", new BigDecimal("10.00"));
        addIngredient(product, emptyStock, new BigDecimal("50"));

        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>(List.of(product)));

        ProductionSuggestionResponse response = service.calculate();

        assertThat(response.items()).isEmpty();
        assertThat(response.grandTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should prioritize higher-value product when two products share same raw material")
    void shouldPrioritizeHigherValueProductOnConflict() {
        // Ambos os produtos precisam de açúcar. O produto de maior valor deve ser produzido primeiro.
        Product expensive = buildProduct(1L, "P-001", "Luxury Candy", new BigDecimal("100.00"));
        addIngredient(expensive, sugar, new BigDecimal("100"));

        Product cheap = buildProduct(2L, "P-002", "Simple Candy", new BigDecimal("10.00"));
        addIngredient(cheap, sugar, new BigDecimal("100"));

        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>(List.of(cheap, expensive)));

        ProductionSuggestionResponse response = service.calculate();

        // Estoque: 500g de açúcar. Caro primeiro: 500/100 = 5 unidades. Restante: 0 -> barato fica sem
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productCode()).isEqualTo("P-001");
        assertThat(response.items().get(0).quantity()).isEqualTo(5);
        assertThat(response.grandTotal()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Should produce both products when stock allows after conflict resolution")
    void shouldProduceBothProductsWhenStockAllows() {
        // Produto A precisa de 300g, Produto B precisa de 200g, estoque: 1000g
        // A: 1000/300 = 3 unidades -> consome 900g
        // B: 100g restantes / 200g = 0 unidades
        RawMaterial bigStock = buildRawMaterial(4L, "RM-004", "BigFlour", new BigDecimal("1000"));

        Product productA = buildProduct(1L, "P-001", "ProductA", new BigDecimal("50.00"));
        addIngredient(productA, bigStock, new BigDecimal("300")); // precisa de 300g por unidade

        Product productB = buildProduct(2L, "P-002", "ProductB", new BigDecimal("20.00"));
        addIngredient(productB, bigStock, new BigDecimal("200")); // precisa de 200g por unidade

        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>(List.of(productA, productB)));

        ProductionSuggestionResponse response = service.calculate();

        // A: 1000/300 = 3 unidades (piso), consome 900g. Restante: 100g
        // B: 100/200 = 0 unidades
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productCode()).isEqualTo("P-001");
        assertThat(response.items().get(0).quantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should produce remaining units of second product after first consumes partial stock")
    void shouldProduceSecondProductWithRemainingStock() {
        RawMaterial material = buildRawMaterial(5L, "RM-005", "Material", new BigDecimal("1000"));

        Product productA = buildProduct(1L, "P-001", "ProductA", new BigDecimal("50.00"));
        addIngredient(productA, material, new BigDecimal("400")); // precisa de 400g -> 2 unidades, consome 800g

        Product productB = buildProduct(2L, "P-002", "ProductB", new BigDecimal("20.00"));
        addIngredient(productB, material, new BigDecimal("100")); // precisa de 100g -> 200g restantes / 100 = 2 unidades

        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>(List.of(productA, productB)));

        ProductionSuggestionResponse response = service.calculate();

        assertThat(response.items()).hasSize(2);

        var itemA = response.items().stream().filter(i -> i.productCode().equals("P-001")).findFirst().orElseThrow();
        var itemB = response.items().stream().filter(i -> i.productCode().equals("P-002")).findFirst().orElseThrow();

        assertThat(itemA.quantity()).isEqualTo(2);
        assertThat(itemB.quantity()).isEqualTo(2);

        // Total: 2*50 + 2*20 = 100 + 40 = 140
        assertThat(response.grandTotal()).isEqualByComparingTo(new BigDecimal("140.00"));
    }

    @Test
    @DisplayName("Should ignore products with no ingredients")
    void shouldIgnoreProductsWithNoIngredients() {
        Product noIngredients = buildProduct(1L, "P-001", "Ghost Product", new BigDecimal("999.00"));
        // nenhum ingrediente adicionado

        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>(List.of(noIngredients)));

        ProductionSuggestionResponse response = service.calculate();

        assertThat(response.items()).isEmpty();
        assertThat(response.grandTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return empty suggestion when there are no products")
    void shouldReturnEmptyWhenNoProducts() {
        when(productRepository.findAllWithIngredients()).thenReturn(new ArrayList<>());

        ProductionSuggestionResponse response = service.calculate();

        assertThat(response.items()).isEmpty();
        assertThat(response.grandTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // --- Auxiliares ---

    private RawMaterial buildRawMaterial(Long id, String code, String name, BigDecimal stock) {
        RawMaterial rm = new RawMaterial();
        rm.setId(id);
        rm.setCode(code);
        rm.setName(name);
        rm.setStockQuantity(stock);
        return rm;
    }

    private Product buildProduct(Long id, String code, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setPrice(price);
        product.setIngredients(new ArrayList<>());
        return product;
    }

    private void addIngredient(Product product, RawMaterial rawMaterial, BigDecimal quantity) {
        ProductIngredient ingredient = new ProductIngredient();
        ingredient.setProduct(product);
        ingredient.setRawMaterial(rawMaterial);
        ingredient.setQuantityRequired(quantity);
        product.getIngredients().add(ingredient);
    }
}

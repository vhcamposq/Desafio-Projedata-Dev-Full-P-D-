package com.projedata.productionmanager.service;

import com.projedata.productionmanager.dto.ProductIngredientRequest;
import com.projedata.productionmanager.dto.ProductRequest;
import com.projedata.productionmanager.entity.Product;
import com.projedata.productionmanager.entity.ProductIngredient;
import com.projedata.productionmanager.entity.RawMaterial;
import com.projedata.productionmanager.repository.ProductRepository;
import com.projedata.productionmanager.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public List<Product> findAll() {
        return productRepository.findAllWithIngredients();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    @Transactional
    public Product create(ProductRequest request) {
        if (productRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Product already exists with code: " + request.code());
        }
        Product product = Product.builder()
                .code(request.code())
                .name(request.name())
                .price(request.price())
                .build();
        addIngredients(product, request.ingredients());
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        productRepository.findByCode(request.code()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Code already in use: " + request.code());
            }
        });
        product.setCode(request.code());
        product.setName(request.name());
        product.setPrice(request.price());
        product.getIngredients().clear();
        addIngredients(product, request.ingredients());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    private void addIngredients(Product product, List<ProductIngredientRequest> ingredientRequests) {
        for (ProductIngredientRequest req : ingredientRequests) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(req.rawMaterialId())
                    .orElseThrow(() -> new IllegalArgumentException("Raw material not found with id: " + req.rawMaterialId()));
            ProductIngredient ingredient = ProductIngredient.builder()
                    .product(product)
                    .rawMaterial(rawMaterial)
                    .quantityRequired(req.quantityRequired())
                    .build();
            product.getIngredients().add(ingredient);
        }
    }
}

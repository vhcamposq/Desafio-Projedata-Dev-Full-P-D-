package com.projedata.productionmanager.repository;

import com.projedata.productionmanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.ingredients i LEFT JOIN FETCH i.rawMaterial")
    List<Product> findAllWithIngredients();
}

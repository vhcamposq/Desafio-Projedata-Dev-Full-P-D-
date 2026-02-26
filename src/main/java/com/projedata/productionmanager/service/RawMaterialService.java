package com.projedata.productionmanager.service;

import com.projedata.productionmanager.dto.RawMaterialRequest;
import com.projedata.productionmanager.entity.RawMaterial;
import com.projedata.productionmanager.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialService {

    private final RawMaterialRepository repository;

    public List<RawMaterial> findAll() {
        return repository.findAll();
    }

    public RawMaterial findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Raw material not found with id: " + id));
    }

    @Transactional
    public RawMaterial create(RawMaterialRequest request) {
        if (repository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Raw material already exists with code: " + request.code());
        }
        RawMaterial rawMaterial = RawMaterial.builder()
                .code(request.code())
                .name(request.name())
                .stockQuantity(request.stockQuantity())
                .build();
        return repository.save(rawMaterial);
    }

    @Transactional
    public RawMaterial update(Long id, RawMaterialRequest request) {
        RawMaterial rawMaterial = findById(id);
        repository.findByCode(request.code()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Code already in use: " + request.code());
            }
        });
        rawMaterial.setCode(request.code());
        rawMaterial.setName(request.name());
        rawMaterial.setStockQuantity(request.stockQuantity());
        return repository.save(rawMaterial);
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial rawMaterial = findById(id);
        repository.delete(rawMaterial);
    }
}

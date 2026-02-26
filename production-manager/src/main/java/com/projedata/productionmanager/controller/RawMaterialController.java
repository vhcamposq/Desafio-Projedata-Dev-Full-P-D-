package com.projedata.productionmanager.controller;

import com.projedata.productionmanager.dto.RawMaterialRequest;
import com.projedata.productionmanager.entity.RawMaterial;
import com.projedata.productionmanager.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService service;

    @GetMapping
    public List<RawMaterial> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public RawMaterial findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<RawMaterial> create(@RequestBody @Valid RawMaterialRequest request) {
        RawMaterial created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public RawMaterial update(@PathVariable Long id, @RequestBody @Valid RawMaterialRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

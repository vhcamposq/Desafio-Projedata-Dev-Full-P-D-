package com.projedata.productionmanager.controller;

import com.projedata.productionmanager.dto.ProductionSuggestionResponse;
import com.projedata.productionmanager.service.ProductionCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionCalculatorController {

    private final ProductionCalculatorService service;

    @GetMapping("/suggestion")
    public ProductionSuggestionResponse getSuggestion() {
        return service.calculate();
    }
}

package com.projedata.productionmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String code;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Min(0)
    private BigDecimal stockQuantity;
}

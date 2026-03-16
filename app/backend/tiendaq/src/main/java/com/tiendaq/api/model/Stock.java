package com.tiendaq.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idStock;

    @Column(name = "fechaingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = false)
    private int stock;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto producto;
}

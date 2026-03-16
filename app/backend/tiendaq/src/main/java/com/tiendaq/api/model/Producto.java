package com.tiendaq.api.model;

import com.tiendaq.api.model.enums.Categoria;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProducto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(name = "nombreproducto", nullable = false, length = 50)
    private String nombre;

    @Column(name = "preciounitario", nullable = false)
    private double precioUnitario;
}

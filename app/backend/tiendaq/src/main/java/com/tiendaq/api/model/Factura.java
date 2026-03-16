package com.tiendaq.api.model;

import java.time.LocalDateTime;

import com.tiendaq.api.model.enums.MetodoPago;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFactura;

    @Column(name = "fechacompra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "totalcompra", nullable = false)
    private double totalCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodopago", nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false)
    private double iva;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idempleado", nullable = false)
    private Empleado empleado;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idcliente", nullable = false)
    private Cliente cliente;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idcarritocompra", nullable = false)
    private Carrito carrito;
}

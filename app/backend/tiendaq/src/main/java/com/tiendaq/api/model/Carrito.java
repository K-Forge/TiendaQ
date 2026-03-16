package com.tiendaq.api.model;

import java.time.LocalDateTime;

import com.tiendaq.api.model.enums.Estado;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CarritoCompra")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcarritocompra")
    private int idCarrito;

    @Column(name = "fechacreacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;
}

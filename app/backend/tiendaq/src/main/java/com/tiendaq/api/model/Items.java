package com.tiendaq.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ItemCarrito")
@IdClass(Items.ItemId.class)
public class Items {

    @Id
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idcarritocompra", nullable = false)
    private Carrito carrito;

    @Id
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "preciounitario", nullable = false)
    private double precioUnitario;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemId implements Serializable {
        private int carrito;
        private int producto;
    }
}

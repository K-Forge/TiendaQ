package com.tiendaq.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Cliente")
@PrimaryKeyJoinColumn(name = "idUsuario")
public class Cliente extends Usuario {

    @Column(name = "idcliente")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCliente;
}

package com.tiendaq.api.model;

import com.tiendaq.api.model.enums.TipoEmpleado;

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
@Table(name = "Empleado")
@PrimaryKeyJoinColumn(name = "idUsuario")
public class Empleado extends Usuario {

    @Column(name = "idempleado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEmpleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoempleado", nullable = false)
    private TipoEmpleado tipoEmpleado;
}

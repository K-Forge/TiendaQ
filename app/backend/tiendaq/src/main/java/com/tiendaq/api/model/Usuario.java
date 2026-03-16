package com.tiendaq.api.model;

import com.tiendaq.api.model.enums.TipoDocumento;
import com.tiendaq.api.model.enums.TipoUsuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @Column(nullable = false, length = 20)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipodocumento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(nullable = false, unique = true, length = 20)
    private String telefono;

    @Column(nullable = false, unique = true, length = 70)
    private String correo;

    @Column(nullable = false, length = 100)
    private String direccion;

    @Column(nullable = false, length = 250)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipousuario", nullable = false)
    private TipoUsuario tipoUsuario;
}

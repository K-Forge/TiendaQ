package com.tiendaq.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendaq.api.model.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    List<Carrito> findByUsuarioIdUsuario(int idUsuario);
}

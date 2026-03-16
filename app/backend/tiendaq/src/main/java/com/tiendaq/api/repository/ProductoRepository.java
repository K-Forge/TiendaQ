package com.tiendaq.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendaq.api.model.Producto;
import com.tiendaq.api.model.enums.Categoria;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByCategoria(Categoria categoria);
}

package com.tiendaq.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendaq.api.model.Producto;
import com.tiendaq.api.model.enums.Categoria;
import com.tiendaq.api.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(int id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Transactional
    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(int id) {
        productoRepository.deleteById(id);
    }
}

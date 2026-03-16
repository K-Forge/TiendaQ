package com.tiendaq.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendaq.api.model.Carrito;
import com.tiendaq.api.repository.CarritoRepository;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    public List<Carrito> buscarPorUsuario(int idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public Optional<Carrito> buscarPorId(int id) {
        return carritoRepository.findById(id);
    }

    @Transactional
    public Carrito crear(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito actualizar(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Transactional
    public void eliminar(int id) {
        carritoRepository.deleteById(id);
    }
}

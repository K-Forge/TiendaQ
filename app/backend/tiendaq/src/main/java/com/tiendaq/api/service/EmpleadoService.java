package com.tiendaq.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tiendaq.api.model.Empleado;
import com.tiendaq.api.repository.EmpleadoRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Optional<Empleado> buscarPorId(int id) {
        return empleadoRepository.findById(id);
    }

    public Optional<Empleado> buscarPorUsuario(int idUsuario) {
        return empleadoRepository.findByIdUsuario(idUsuario);
    }
}

package com.tiendaq.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendaq.api.model.Factura;
import com.tiendaq.api.repository.FacturaRepository;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> buscarPorCliente(int idCliente) {
        return facturaRepository.findByClienteIdCliente(idCliente);
    }

    public Optional<Factura> buscarPorId(int id) {
        return facturaRepository.findById(id);
    }

    @Transactional
    public Factura crear(Factura factura) {
        return facturaRepository.save(factura);
    }
}

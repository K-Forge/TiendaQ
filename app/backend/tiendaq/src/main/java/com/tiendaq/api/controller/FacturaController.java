package com.tiendaq.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiendaq.api.model.Factura;
import com.tiendaq.api.service.FacturaService;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/cliente/{idCliente}")
    public List<Factura> buscarPorCliente(@PathVariable int idCliente) {
        return facturaService.buscarPorCliente(idCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> buscarPorId(@PathVariable int id) {
        return facturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Factura crear(@RequestBody Factura factura) {
        return facturaService.crear(factura);
    }
}

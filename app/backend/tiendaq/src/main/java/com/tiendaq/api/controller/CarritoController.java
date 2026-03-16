package com.tiendaq.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiendaq.api.model.Carrito;
import com.tiendaq.api.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Carrito> buscarPorUsuario(@PathVariable int idUsuario) {
        return carritoService.buscarPorUsuario(idUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> buscarPorId(@PathVariable int id) {
        return carritoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Carrito crear(@RequestBody Carrito carrito) {
        return carritoService.crear(carrito);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizar(@PathVariable int id, @RequestBody Carrito carrito) {
        return carritoService.buscarPorId(id)
                .map(existing -> {
                    carrito.setIdCarrito(id);
                    return ResponseEntity.ok(carritoService.actualizar(carrito));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return carritoService.buscarPorId(id)
                .map(existing -> {
                    carritoService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

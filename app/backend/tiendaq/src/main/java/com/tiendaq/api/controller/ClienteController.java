package com.tiendaq.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiendaq.api.model.Cliente;
import com.tiendaq.api.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Cliente> buscarPorUsuario(@PathVariable int idUsuario) {
        return clienteService.buscarPorUsuario(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cliente crear(@RequestBody Cliente cliente) {
        return clienteService.crear(cliente);
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<Cliente> actualizar(@PathVariable int idUsuario, @RequestBody Cliente cliente) {
        return clienteService.buscarPorUsuario(idUsuario)
                .map(existing -> {
                    cliente.setIdUsuario(idUsuario);
                    return ResponseEntity.ok(clienteService.actualizar(cliente));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

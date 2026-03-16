package com.tiendaq.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiendaq.api.model.Stock;
import com.tiendaq.api.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/producto/{idProducto}")
    public List<Stock> listarPorProducto(@PathVariable int idProducto) {
        return stockService.listarPorProducto(idProducto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> buscarPorId(@PathVariable int id) {
        return stockService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Stock crear(@RequestBody Stock stock) {
        return stockService.crear(stock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> actualizar(@PathVariable int id, @RequestBody Stock stock) {
        return stockService.buscarPorId(id)
                .map(existing -> {
                    stock.setIdStock(id);
                    return ResponseEntity.ok(stockService.actualizar(stock));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return stockService.buscarPorId(id)
                .map(existing -> {
                    stockService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

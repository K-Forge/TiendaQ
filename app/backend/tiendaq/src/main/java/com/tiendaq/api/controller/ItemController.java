package com.tiendaq.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiendaq.api.model.Items;
import com.tiendaq.api.service.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/carrito/{idCarrito}")
    public List<Items> buscarPorCarrito(@PathVariable int idCarrito) {
        return itemService.buscarPorCarrito(idCarrito);
    }

    @PostMapping
    public Items crear(@RequestBody Items item) {
        return itemService.crear(item);
    }

    @PutMapping
    public Items actualizar(@RequestBody Items item) {
        return itemService.actualizar(item);
    }

    @DeleteMapping("/{idCarrito}/{idProducto}")
    public ResponseEntity<Void> eliminar(@PathVariable int idCarrito, @PathVariable int idProducto) {
        Items.ItemId id = new Items.ItemId(idCarrito, idProducto);
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

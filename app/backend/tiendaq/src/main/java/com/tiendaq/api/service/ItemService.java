package com.tiendaq.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendaq.api.model.Items;
import com.tiendaq.api.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Items> buscarPorCarrito(int idCarrito) {
        return itemRepository.findByCarritoIdCarrito(idCarrito);
    }

    public List<Items> buscarPorProducto(int idProducto) {
        return itemRepository.findByProductoIdProducto(idProducto);
    }

    @Transactional
    public Items crear(Items item) {
        return itemRepository.save(item);
    }

    @Transactional
    public Items actualizar(Items item) {
        return itemRepository.save(item);
    }

    @Transactional
    public void eliminar(Items.ItemId id) {
        itemRepository.deleteById(id);
    }
}

package com.tiendaq.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendaq.api.model.Items;

@Repository
public interface ItemRepository extends JpaRepository<Items, Items.ItemId> {

    List<Items> findByCarritoIdCarrito(int idCarrito);

    List<Items> findByProductoIdProducto(int idProducto);
}

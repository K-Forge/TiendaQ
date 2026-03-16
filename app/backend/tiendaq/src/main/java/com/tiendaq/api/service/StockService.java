package com.tiendaq.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiendaq.api.model.Stock;
import com.tiendaq.api.repository.StockRepository;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> listarPorProducto(int idProducto) {
        return stockRepository.findByProductoIdProducto(idProducto);
    }

    public Optional<Stock> buscarPorId(int id) {
        return stockRepository.findById(id);
    }

    @Transactional
    public Stock crear(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional
    public Stock actualizar(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional
    public void eliminar(int id) {
        stockRepository.deleteById(id);
    }
}

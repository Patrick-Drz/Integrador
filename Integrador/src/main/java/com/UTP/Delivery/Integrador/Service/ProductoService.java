package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto saveProducto(Producto producto) {
        // Aquí puedes añadir validaciones adicionales antes de guardar
        return productoRepository.save(producto);
    }

    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto updateProducto(Producto producto) {
        if (producto.getId() == null || !productoRepository.existsById(producto.getId())) {
            throw new IllegalArgumentException("El producto con ID " + producto.getId() + " no existe.");
        }
        return productoRepository.save(producto);
    }
}
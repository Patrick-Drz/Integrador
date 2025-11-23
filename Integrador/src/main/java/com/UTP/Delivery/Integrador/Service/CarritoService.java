package com.UTP.Delivery.Integrador.Service; // Este es el package de tu archivo

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.UTP.Delivery.Integrador.Model.Carrito;
import com.UTP.Delivery.Integrador.Model.ItemCarrito;
import com.UTP.Delivery.Integrador.Model.Oferta;
import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Repository.CarritoRepository;
import com.UTP.Delivery.Integrador.Repository.ItemCarritoRepository;

@Service
public class CarritoService {

    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ItemCarritoRepository itemCarritoRepository;
    @Autowired private ProductoService productoService;
    @Autowired private OfertaService ofertaService;

    @Transactional
    public Carrito getOrCreateCarritoForUser(User user) {
        return carritoRepository.findByUsuarioAndEstado(user, "ACTIVO")
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(user);
                    nuevoCarrito.setEstado("ACTIVO");
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    @Transactional
    public Carrito addItemToCarrito(Long carritoId, Long productoId, Long ofertaId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Carrito carrito = carritoRepository.findCarritoById(carritoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoId));

        Optional<ItemCarrito> existingItem = carrito.getItems().stream()
                .filter(item -> (productoId != null && item.getProducto() != null && item.getProducto().getId().equals(productoId)) ||
                        (ofertaId != null && item.getOferta() != null && item.getOferta().getId().equals(ofertaId)))
                .findFirst();

        if (existingItem.isPresent()) {
            ItemCarrito itemToUpdate = existingItem.get();
            int nuevaCantidad = itemToUpdate.getCantidad() + cantidad;

            if (itemToUpdate.getProducto() != null) {
                if (itemToUpdate.getProducto().getStock() < nuevaCantidad) {
                    throw new IllegalArgumentException("Stock insuficiente.");
                }
            }
            itemToUpdate.setCantidad(nuevaCantidad);
            itemCarritoRepository.save(itemToUpdate); 
        } else {
            ItemCarrito newItem = new ItemCarrito();
            newItem.setCarrito(carrito);
            newItem.setCantidad(cantidad);

            if (productoId != null) {
                Producto producto = productoService.getProductoById(productoId).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
                if (producto.getStock() < cantidad) throw new IllegalArgumentException("Stock insuficiente.");
                newItem.setProducto(producto);
                newItem.setPrecioUnitarioAlMomento(producto.getPrecio());
            } else if (ofertaId != null) {
                Oferta oferta = ofertaService.getOfertaById(ofertaId).orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada."));
                if (!oferta.getActiva()) throw new IllegalArgumentException("La oferta no está activa.");
                newItem.setOferta(oferta);
                newItem.setPrecioUnitarioAlMomento(oferta.getPrecioOferta());
            }

            newItem = itemCarritoRepository.save(newItem); 
            carrito.getItems().add(newItem); 
        }

        carrito.setFechaActualizacion(LocalDateTime.now());
        return carritoRepository.save(carrito); 
    }

    @Transactional
    public void updateItemQuantity(Long itemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItemFromCarrito(itemId);
            return;
        }

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Ítem no encontrado"));

        Carrito carrito = carritoRepository.findCarritoById(item.getCarrito().getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrito del ítem no encontrado"));

        if (item.getProducto() != null) {
            if (item.getProducto().getStock() < newQuantity) {
                throw new IllegalArgumentException("Stock insuficiente.");
            }
        }
        
        item.setCantidad(newQuantity);
        itemCarritoRepository.save(item);

        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    @Transactional
    public void removeItemFromCarrito(Long itemId) {
        if (itemId == null) {
            return;
        }
        
        itemCarritoRepository.deleteItemById(itemId);
    }

    public BigDecimal calcularTotalCarrito(Carrito carrito) {
        if (carrito == null || carrito.getId() == null) return BigDecimal.ZERO;

        Carrito carritoActualizado = carritoRepository.findById(carrito.getId()).orElse(carrito);

        if (carritoActualizado.getItems() == null || carritoActualizado.getItems().isEmpty()) { return BigDecimal.ZERO; }
        return carritoActualizado.getItems().stream()
                .map(item -> item.getPrecioUnitarioAlMomento().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Carrito> getCarritoById(Long id) { return carritoRepository.findById(id); }

    public List<Carrito> getAllCarritos() { return carritoRepository.findAll(); }
}
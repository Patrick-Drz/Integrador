package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.Carrito;
import com.UTP.Delivery.Integrador.Model.ItemCarrito;
import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Model.Oferta;
import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Repository.CarritoRepository;
import com.UTP.Delivery.Integrador.Repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private OfertaService ofertaService;

    @Transactional
    public Carrito getOrCreateCarritoForUser(User user) {
        Optional<Carrito> existingCarrito = carritoRepository.findByUsuarioAndEstado(user, "ACTIVO");
        if (existingCarrito.isPresent()) {
            return existingCarrito.get();
        } else {
            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setUsuario(user);
            nuevoCarrito.setEstado("ACTIVO");
            nuevoCarrito.setFechaCreacion(LocalDateTime.now());
            return carritoRepository.save(nuevoCarrito);
        }
    }

    @Transactional
    public Carrito addItemToCarrito(Long carritoId, Long productoId, Long ofertaId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoId));

        Optional<ItemCarrito> existingItem = carrito.getItems().stream()
                .filter(item -> (productoId != null && item.getProducto() != null && item.getProducto().getId().equals(productoId)) ||
                        (ofertaId != null && item.getOferta() != null && item.getOferta().getId().equals(ofertaId)))
                .findFirst();

        if (existingItem.isPresent()) {
            ItemCarrito itemToUpdate = existingItem.get();

            if (productoId != null) {
                Producto p = itemToUpdate.getProducto();
                if (p != null && p.getStock() < (itemToUpdate.getCantidad() + cantidad)) {
                    throw new IllegalArgumentException("No hay suficiente stock para añadir " + cantidad + " unidades de " + p.getNombre() + " al carrito.");
                }
            }
            itemToUpdate.setCantidad(itemToUpdate.getCantidad() + cantidad);

        } else {
            ItemCarrito newItem = new ItemCarrito();
            newItem.setCarrito(carrito);
            newItem.setCantidad(cantidad);

            if (productoId != null) {
                Producto producto = productoService.getProductoById(productoId)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));
                if (producto.getStock() < cantidad) {
                    throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre());
                }
                newItem.setProducto(producto);
                newItem.setPrecioUnitarioAlMomento(producto.getPrecio());
                newItem.setOferta(null);
            } else if (ofertaId != null) {
                Oferta oferta = ofertaService.getOfertaById(ofertaId)
                        .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada con ID: " + ofertaId));
                if (!oferta.getActiva()) {
                    throw new IllegalArgumentException("La oferta '" + oferta.getNombreOferta() + "' no está activa.");
                }
                newItem.setOferta(oferta);
                newItem.setPrecioUnitarioAlMomento(oferta.getPrecioOferta());
                newItem.setProducto(null);
            } else {
                throw new IllegalArgumentException("Debe especificar un ID de producto o un ID de oferta.");
            }
            carrito.getItems().add(newItem);
        }
        carrito.setFechaActualizacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    @Transactional
    public void updateItemQuantity(Long itemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Ítem de carrito no encontrado con ID: " + itemId));

        if (item.getProducto() != null) {
            if (item.getProducto().getStock() < newQuantity) {
                throw new IllegalArgumentException("No hay suficiente stock para la cantidad solicitada de " + item.getProducto().getNombre() + ".");
            }
        }
        item.setCantidad(newQuantity);
        itemCarritoRepository.save(item);

        Carrito carrito = item.getCarrito();
        if(carrito != null) {
            carrito.setFechaActualizacion(LocalDateTime.now());
            carritoRepository.save(carrito);
        }
    }

    @Transactional
    public void removeItemFromCarrito(Long itemId) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Ítem de carrito no encontrado con ID: " + itemId));

        Carrito carrito = item.getCarrito();
        if (carrito != null) {
            carrito.getItems().remove(item);
            carrito.setFechaActualizacion(LocalDateTime.now());
            carritoRepository.save(carrito);
        }
        itemCarritoRepository.delete(item);
    }

    public BigDecimal calcularTotalCarrito(Carrito carrito) {
        if (carrito == null || carrito.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return carrito.getItems().stream()
                .map(item -> item.getPrecioUnitarioAlMomento().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Carrito> getCarritoById(Long id) {
        return carritoRepository.findById(id);
    }

    public List<Carrito> getAllCarritos() {
        return carritoRepository.findAll();
    }
}
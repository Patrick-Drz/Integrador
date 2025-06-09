package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.*;
import com.UTP.Delivery.Integrador.Repository.OrdenVentaRepository;
import com.UTP.Delivery.Integrador.Repository.DetalleOrdenVentaRepository;
import com.UTP.Delivery.Integrador.Repository.CarritoRepository;
import com.UTP.Delivery.Integrador.Repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Service
public class VentaService {

    @Autowired
    private OrdenVentaRepository ordenVentaRepository;
    @Autowired
    private DetalleOrdenVentaRepository detalleOrdenVentaRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Transactional
    public OrdenVenta procesarVentaDesdeCarrito(User usuario, Carrito carrito, Ubicacion ubicacionEntrega) {
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío. No se puede procesar una venta.");
        }
        if (ubicacionEntrega == null) {
            throw new IllegalArgumentException("No se ha seleccionado una ubicación de entrega.");
        }

        BigDecimal totalCarrito = carrito.getItems().stream()
                .map(item -> item.getPrecioUnitarioAlMomento().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrdenVenta ordenVenta = new OrdenVenta(usuario, LocalDateTime.now(), totalCarrito, ubicacionEntrega);
        ordenVenta = ordenVentaRepository.save(ordenVenta);

        List<DetalleOrdenVenta> detallesOrden = new ArrayList<>();
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            DetalleOrdenVenta detalle = new DetalleOrdenVenta(
                    ordenVenta,
                    itemCarrito.getProducto(),
                    itemCarrito.getOferta(),
                    itemCarrito.getCantidad(),
                    itemCarrito.getPrecioUnitarioAlMomento()
            );
            detallesOrden.add(detalle);
        }
        ordenVenta.setItems(detallesOrden);
        detalleOrdenVentaRepository.saveAll(detallesOrden);

        itemCarritoRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return ordenVenta;
    }

    @Transactional(readOnly = true)
    public List<OrdenVenta> getAllOrdenesVenta() {
        List<OrdenVenta> ordenes = ordenVentaRepository.findAll();

        for (OrdenVenta orden : ordenes) {
            if (orden.getUsuario() != null) {
                orden.getUsuario().getNombreCompleto();
                orden.getUsuario().getCodigoEstudiante();
                orden.getUsuario().getCorreo();
            }

            if (orden.getUbicacionEntrega() != null) {
                orden.getUbicacionEntrega().getPiso();
                orden.getUbicacionEntrega().getCodigoAula();
            }

            if (orden.getItems() != null) {
                orden.getItems().size();
                for (DetalleOrdenVenta item : orden.getItems()) {
                    if (item.getProducto() != null) {
                        item.getProducto().getNombre();
                    }
                    if (item.getOferta() != null) {
                        item.getOferta().getNombreOferta();
                    }
                }
            }
        }
        return ordenes;
    }

    @Transactional(readOnly = true)
    public List<OrdenVenta> getOrdenesVentaByUsuario(User usuario) {
        List<OrdenVenta> ordenes = ordenVentaRepository.findByUsuario(usuario);
        for (OrdenVenta orden : ordenes) {
            if (orden.getUsuario() != null) orden.getUsuario().getNombreCompleto();
            if (orden.getUbicacionEntrega() != null) orden.getUbicacionEntrega().getPiso();
            if (orden.getItems() != null) {
                orden.getItems().size();
                for (DetalleOrdenVenta item : orden.getItems()) {
                    if (item.getProducto() != null) item.getProducto().getNombre();
                    if (item.getOferta() != null) item.getOferta().getNombreOferta();
                }
            }
        }
        return ordenes;
    }
}
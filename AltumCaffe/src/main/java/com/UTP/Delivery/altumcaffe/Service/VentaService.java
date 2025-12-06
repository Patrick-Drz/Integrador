package com.UTP.Delivery.altumcaffe.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.UTP.Delivery.altumcaffe.Model.Carrito;
import com.UTP.Delivery.altumcaffe.Model.DetalleOrdenVenta;
import com.UTP.Delivery.altumcaffe.Model.ItemCarrito;
import com.UTP.Delivery.altumcaffe.Model.OrdenVenta;
import com.UTP.Delivery.altumcaffe.Model.Producto;
import com.UTP.Delivery.altumcaffe.Model.Ubicacion;
import com.UTP.Delivery.altumcaffe.Model.User;
import com.UTP.Delivery.altumcaffe.Repository.CarritoRepository;
import com.UTP.Delivery.altumcaffe.Repository.DetalleOrdenVentaRepository;
import com.UTP.Delivery.altumcaffe.Repository.ItemCarritoRepository;
import com.UTP.Delivery.altumcaffe.Repository.OrdenVentaRepository;
import com.UTP.Delivery.altumcaffe.Repository.ProductoRepository;

@Service
public class VentaService {

    @Autowired private OrdenVentaRepository ordenVentaRepository;
    @Autowired private DetalleOrdenVentaRepository detalleOrdenVentaRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ItemCarritoRepository itemCarritoRepository;
    @Autowired private ProductoRepository productoRepository; 

    @Transactional
    public OrdenVenta procesarVentaDesdeCarrito(User usuario, Carrito carrito, Ubicacion ubicacionEntrega) {
        
        if (carrito == null || carrito.getId() == null) {
            throw new IllegalArgumentException("El carrito es inválido.");
        }
        if (ubicacionEntrega == null) {
            throw new IllegalArgumentException("No se ha seleccionado una ubicación de entrega.");
        }

        Carrito carritoBloqueado = carritoRepository.findCarritoById(carrito.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado o no se pudo bloquear."));

        if (carritoBloqueado.getItems() == null || carritoBloqueado.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        List<ItemCarrito> itemsACopiar = new ArrayList<>(carritoBloqueado.getItems());
        BigDecimal totalCarrito = BigDecimal.ZERO; 

        OrdenVenta ordenVenta = new OrdenVenta(usuario, LocalDateTime.now(), totalCarrito, ubicacionEntrega);
        ordenVenta = ordenVentaRepository.save(ordenVenta);

        List<DetalleOrdenVenta> detallesOrden = new ArrayList<>();
        
        for (ItemCarrito itemCarrito : itemsACopiar) {
            
            if (itemCarrito.getProducto() != null) {
                Producto producto = productoRepository.findById(itemCarrito.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemCarrito.getProducto().getNombre()));

                if (producto.getStock() < itemCarrito.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
                }
                
                producto.setStock(producto.getStock() - itemCarrito.getCantidad());
                productoRepository.save(producto);
            }
            

            DetalleOrdenVenta detalle = new DetalleOrdenVenta(
                    ordenVenta,
                    itemCarrito.getProducto(),
                    itemCarrito.getOferta(),
                    itemCarrito.getCantidad(),
                    itemCarrito.getPrecioUnitarioAlMomento()
            );
            detallesOrden.add(detalle);
            
            totalCarrito = totalCarrito.add(
                itemCarrito.getPrecioUnitarioAlMomento().multiply(BigDecimal.valueOf(itemCarrito.getCantidad()))
            );
        }
        
        detalleOrdenVentaRepository.saveAll(detallesOrden);
        
        ordenVenta.setTotal(totalCarrito);
        ordenVenta.setItems(detallesOrden);
        ordenVentaRepository.save(ordenVenta);

        itemCarritoRepository.deleteByCarritoId(carritoBloqueado.getId());

        return ordenVenta;
    }

    @Transactional(readOnly = true)
    public List<OrdenVenta> getAllOrdenesVenta() {
        List<OrdenVenta> ordenes = ordenVentaRepository.findAll();
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
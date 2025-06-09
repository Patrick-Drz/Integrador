package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.ItemCarrito;
import com.UTP.Delivery.Integrador.Model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    Optional<ItemCarrito> findByCarritoAndProductoId(Carrito carrito, Long productoId);
    Optional<ItemCarrito> findByCarritoAndOfertaId(Carrito carrito, Long ofertaId);
}
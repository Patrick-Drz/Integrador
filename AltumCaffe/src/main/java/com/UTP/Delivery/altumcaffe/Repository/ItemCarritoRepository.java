package com.UTP.Delivery.altumcaffe.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.UTP.Delivery.altumcaffe.Model.Carrito;
import com.UTP.Delivery.altumcaffe.Model.ItemCarrito;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    Optional<ItemCarrito> findByCarritoAndProductoId(Carrito carrito, Long productoId);
    Optional<ItemCarrito> findByCarritoAndOfertaId(Carrito carrito, Long ofertaId);

    @Modifying 
    @Query("DELETE FROM ItemCarrito i WHERE i.carrito.id = :carritoId")
    void deleteByCarritoId(@Param("carritoId") Long carritoId);

    @Modifying
    @Query("DELETE FROM ItemCarrito ic WHERE ic.id = :itemId")
    void deleteItemById(@Param("itemId") Long itemId);
}
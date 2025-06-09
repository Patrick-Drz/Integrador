package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.DetalleOrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleOrdenVentaRepository extends JpaRepository<DetalleOrdenVenta, Long> {
}

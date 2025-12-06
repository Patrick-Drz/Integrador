package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.DetalleOrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleOrdenVentaRepository extends JpaRepository<DetalleOrdenVenta, Long> {
}

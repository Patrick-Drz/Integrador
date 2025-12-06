package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.OrdenVenta;
import com.UTP.Delivery.altumcaffe.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Long> {
    List<OrdenVenta> findByUsuario(User usuario);
}
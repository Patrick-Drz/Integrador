package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
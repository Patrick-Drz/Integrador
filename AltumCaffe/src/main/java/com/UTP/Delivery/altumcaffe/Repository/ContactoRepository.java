package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
}
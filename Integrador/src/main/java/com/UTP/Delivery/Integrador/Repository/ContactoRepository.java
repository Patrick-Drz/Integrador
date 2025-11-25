package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
}
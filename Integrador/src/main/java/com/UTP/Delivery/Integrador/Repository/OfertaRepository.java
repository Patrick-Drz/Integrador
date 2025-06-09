package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {
}
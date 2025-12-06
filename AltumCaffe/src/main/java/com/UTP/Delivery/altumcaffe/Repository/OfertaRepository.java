package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {
}
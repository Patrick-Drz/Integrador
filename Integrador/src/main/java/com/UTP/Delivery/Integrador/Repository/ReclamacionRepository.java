package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamacionRepository extends JpaRepository<Reclamacion, Long> {
}

package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.Ubicacion;
import com.UTP.Delivery.Integrador.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    Optional<Ubicacion> findByUsuario(User usuario);
    Optional<Ubicacion> findByUsuarioAndCodigoAula(User usuario, String codigoAula);
    List<Ubicacion> findAllByUsuario(User usuario);
}

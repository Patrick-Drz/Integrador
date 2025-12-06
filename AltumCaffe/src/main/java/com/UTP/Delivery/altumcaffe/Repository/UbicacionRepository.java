package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.Ubicacion;
import com.UTP.Delivery.altumcaffe.Model.User;
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

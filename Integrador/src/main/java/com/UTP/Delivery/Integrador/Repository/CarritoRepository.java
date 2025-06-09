package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.Carrito;
import com.UTP.Delivery.Integrador.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioAndEstado(User usuario, String estado);

}

package com.UTP.Delivery.altumcaffe.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.UTP.Delivery.altumcaffe.Model.Carrito;
import com.UTP.Delivery.altumcaffe.Model.User;

import jakarta.persistence.LockModeType;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioAndEstado(User usuario, String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Carrito> findCarritoById(Long id);
}
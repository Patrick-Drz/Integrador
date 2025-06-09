package com.UTP.Delivery.Integrador.Repository;

import com.UTP.Delivery.Integrador.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByCorreoAndContrasena(String correo, String contrasena);
}

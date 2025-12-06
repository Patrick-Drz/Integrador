package com.UTP.Delivery.altumcaffe.Repository;

import com.UTP.Delivery.altumcaffe.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCorreo(String correo);
    Optional<User> findByCorreoAndContrasena(String correo, String contrasena);
    Optional<User> findByCodigoEstudiante(String codigoEstudiante);
    
    Optional<User> findByNombreCompleto(String nombreCompleto);
}
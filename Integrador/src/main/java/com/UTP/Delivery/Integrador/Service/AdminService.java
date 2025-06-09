package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.Admin;
import com.UTP.Delivery.Integrador.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin loginAdmin(String correo, String contrasena) {
        Optional<Admin> adminOptional = adminRepository.findByCorreoAndContrasena(correo, contrasena);

        if (adminOptional.isPresent()) {
            return adminOptional.get();
        } else {
            throw new IllegalArgumentException("Credenciales de administrador incorrectas.");
        }
    }
}
package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.Admin;
import com.UTP.Delivery.Integrador.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    private static final String ADMIN_EMAIL = "Admin123@utp.edu.pe";
    private static final String ADMIN_PASSWORD = "admin123";

    public Admin loginAdmin(String correo, String contrasena) {
        if (!ADMIN_EMAIL.equals(correo) || !ADMIN_PASSWORD.equals(contrasena)) {
            throw new IllegalArgumentException("Credenciales de administrador incorrectas.");
        }
        return adminRepository.findByCorreoAndContrasena(correo, contrasena)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales de administrador incorrectas."));
    }
}
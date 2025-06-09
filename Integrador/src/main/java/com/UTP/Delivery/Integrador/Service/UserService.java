package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final String UTP_EMAIL_REGEX = "^u\\d{8}@utp\\.edu\\.pe$"; // u12345678@utp.edu.pe

    public User registerUser(String correo, String contrasena) {
        if (!isValidUtpEmail(correo)) {
            throw new IllegalArgumentException("El formato del correo debe ser u########@utp.edu.pe");
        }
        if (contrasena.length() <= 6) {
            throw new IllegalArgumentException("La contraseña debe tener más de 6 dígitos.");
        }
        if (userRepository.findByCorreo(correo).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        User newUser = new User();
        newUser.setCorreo(correo);
        newUser.setContrasena(contrasena);
        return userRepository.save(newUser);
    }

    public User loginUser(String correo, String contrasena) {
        return userRepository.findByCorreoAndContrasena(correo, contrasena)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales de usuario incorrectas."));
    }

    private boolean isValidUtpEmail(String email) {
        return Pattern.matches(UTP_EMAIL_REGEX, email);
    }
}
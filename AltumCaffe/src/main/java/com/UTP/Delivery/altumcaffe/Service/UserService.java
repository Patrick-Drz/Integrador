package com.UTP.Delivery.altumcaffe.Service;

import com.UTP.Delivery.altumcaffe.Model.User;
import com.UTP.Delivery.altumcaffe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CODIGO_ESTUDIANTE_REGEX = "^[uU]\\d{8}$";

    public User registerUser(String email, String password, String nombreCompleto, String codigoEstudiante) {
        
        if (userRepository.findByCorreo(email).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (userRepository.findByCodigoEstudiante(codigoEstudiante).isPresent()) {
            throw new IllegalArgumentException("El código de estudiante ya está registrado.");
        }
        if (userRepository.findByNombreCompleto(nombreCompleto).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese Nombre Completo.");
        }
        if (!isValidCodigoEstudiante(codigoEstudiante)) {
            throw new IllegalArgumentException("El formato del código de estudiante es inválido. Debe ser 'u' o 'U' seguida de 8 dígitos.");
        }

        User newUser = new User();
        newUser.setCorreo(email);
        newUser.setNombreCompleto(nombreCompleto);
        newUser.setCodigoEstudiante(codigoEstudiante);

        String hashedPassword = passwordEncoder.encode(password);
        newUser.setContrasena(hashedPassword);

        newUser.setRol("ROLE_USER");

        return userRepository.save(newUser);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByCorreo(String correo) {
        return userRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + correo));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private boolean isValidCodigoEstudiante(String codigo) {
        return Pattern.matches(CODIGO_ESTUDIANTE_REGEX, codigo);
    }
}
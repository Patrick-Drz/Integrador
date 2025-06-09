package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final String CODIGO_ESTUDIANTE_REGEX = "^[uU]\\d{8}$";

    /**
     * Registra un nuevo usuario en el sistema.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario (se debería hashear).
     * @param nombreCompleto Nombre completo del usuario.
     * @param codigoEstudiante Código de estudiante del usuario.
     * @return El usuario registrado.
     * @throws IllegalArgumentException Si el correo o código de estudiante ya existen, o el formato es inválido.
     */
    public User registerUser(String email, String password, String nombreCompleto, String codigoEstudiante) {
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                nombreCompleto == null || nombreCompleto.trim().isEmpty() ||
                codigoEstudiante == null || codigoEstudiante.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        if (!isValidCodigoEstudiante(codigoEstudiante)) {
            throw new IllegalArgumentException("El formato del código de estudiante es inválido. Debe ser 'u' o 'U' seguida de 8 dígitos.");
        }

        if (userRepository.findByCorreo(email).isPresent()) { // findByCorreo ahora devuelve Optional
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        if (userRepository.findByCodigoEstudiante(codigoEstudiante).isPresent()) { // findByCodigoEstudiante ahora devuelve Optional
            throw new IllegalArgumentException("El código de estudiante ya está registrado.");
        }

        User newUser = new User(email, password, nombreCompleto, codigoEstudiante);

        return userRepository.save(newUser);
    }

    /**
     * Intenta autenticar un usuario.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return El objeto User si las credenciales son válidas y el usuario está activo, null en caso contrario.
     */
    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByCorreo(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getContrasena().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByCorreo(String correo) {
        return userRepository.findByCorreo(correo).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + correo));
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) { // ID es Long
        userRepository.deleteById(id);
    }

    private boolean isValidCodigoEstudiante(String codigo) {
        return Pattern.matches(CODIGO_ESTUDIANTE_REGEX, codigo);
    }
}
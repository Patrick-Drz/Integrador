package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/register-ajax")
    @ResponseBody
    public Map<String, Object> registerAjax(@RequestParam("registerEmail") String email,
                                            @RequestParam("registerPassword") String password,
                                            @RequestParam("registerNombreCompleto") String nombreCompleto,
                                            @RequestParam("registerCodigoEstudiante") String codigoEstudiante) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.registerUser(email, password, nombreCompleto, codigoEstudiante);
            response.put("success", true);
            response.put("message", "Registro exitoso. ¡Ahora puedes iniciar sesión!");

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (NullPointerException e) {
            response.put("success", false);
            response.put("message", "ERROR: PasswordEncoder es nulo. Verifica que tu archivo SecurityConfig.java esté en el paquete 'config'.");
            e.printStackTrace(); 

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error genérico en el registro. Inténtalo de nuevo más tarde.");
            e.printStackTrace();
        }
        return response;
    }
}
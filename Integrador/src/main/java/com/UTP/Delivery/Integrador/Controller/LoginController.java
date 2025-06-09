package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.Admin;
import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Service.AdminService;
import com.UTP.Delivery.Integrador.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String loginEmail,
                            @RequestParam String loginPassword,
                            RedirectAttributes redirectAttributes) {
        try {
            String targetUrl;
            if (loginEmail.equals("Admin123@utp.edu.pe")) {
                adminService.loginAdmin(loginEmail, loginPassword);
                redirectAttributes.addFlashAttribute("loginMessage", "Inicio de sesión Correcto como Administrador.");
                redirectAttributes.addFlashAttribute("loginSuccess", true);
                targetUrl = "/indexAdmin";
            } else {
                userService.loginUser(loginEmail, loginPassword);
                redirectAttributes.addFlashAttribute("loginMessage", "Inicio de sesión Correcto como Usuario.");
                redirectAttributes.addFlashAttribute("loginSuccess", true);
                targetUrl = "/indexUsuario";
            }
            redirectAttributes.addFlashAttribute("redirectUrl", targetUrl);
            return "redirect:/"; // Redirige a la página de login/registro para mostrar el modal
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loginMessage", "Error al iniciar sesión: " + e.getMessage());
            redirectAttributes.addFlashAttribute("loginSuccess", false);
            redirectAttributes.addFlashAttribute("redirectUrl", null);
            return "redirect:/";
        }
    }

    @PostMapping("/register-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerUserAjax(@RequestParam String registerEmail,
                                                                @RequestParam String registerPassword) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.registerUser(registerEmail, registerPassword);
            response.put("message", "Usuario registrado correctamente.");
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("message", "Error al registrar usuario: " + e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    //@GetMapping("/indexAdmin")
    //public String adminPage() {
    //    return "indexAdmin";
    //}

    //@GetMapping("/indexUsuario")
    //public String userPage() {
    //    return "indexUsuario";
    //}
}
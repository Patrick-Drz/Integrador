package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.Admin;
import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Service.AdminService;
import com.UTP.Delivery.Integrador.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("loginEmail") String email,
                        @RequestParam("loginPassword") String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        try {
            boolean isAdminLoginSuccessful = false;
            try {
                adminService.loginAdmin(email, password);
                isAdminLoginSuccessful = true;
            } catch (IllegalArgumentException e) {
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("loginMessage", "Ocurrió un error al intentar iniciar sesión como administrador.");
                redirectAttributes.addFlashAttribute("loginSuccess", false);
                return "redirect:/login"; // Vuelve a la página de login con el error
            }

            if (isAdminLoginSuccessful) {
                session.setAttribute("userEmail", email);
                session.setAttribute("isAdmin", true);
                redirectAttributes.addFlashAttribute("loginMessage", "¡Bienvenido, Administrador!");
                redirectAttributes.addFlashAttribute("loginSuccess", true);
                redirectAttributes.addFlashAttribute("redirectUrl", "/indexAdmin");
                return "redirect:/login";
            }

            User user = userService.login(email, password);
            if (user != null) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("userEmail", user.getCorreo());
                session.setAttribute("isAdmin", false);
                // Establecemos los atributos para la modal de éxito en el login
                redirectAttributes.addFlashAttribute("loginMessage", "¡Bienvenid@, " + user.getNombreCompleto() + "!");
                redirectAttributes.addFlashAttribute("loginSuccess", true);
                redirectAttributes.addFlashAttribute("redirectUrl", "/indexUsuario");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("loginMessage", "Correo o contraseña incorrectos.");
                redirectAttributes.addFlashAttribute("loginSuccess", false);
                return "redirect:/login";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loginMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("loginSuccess", false);
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loginMessage", "Ocurrió un error inesperado al iniciar sesión.");
            redirectAttributes.addFlashAttribute("loginSuccess", false);
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("loginMessage", "Has cerrado sesión correctamente.");
        redirectAttributes.addFlashAttribute("loginSuccess", true);
        redirectAttributes.addFlashAttribute("redirectUrl", "/login");
        return "redirect:/login";
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
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el registro. Inténtalo de nuevo más tarde.");
            e.printStackTrace();
        }
        return response;
    }
}
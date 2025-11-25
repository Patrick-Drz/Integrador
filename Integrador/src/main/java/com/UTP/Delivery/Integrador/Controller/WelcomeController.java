package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class WelcomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/welcome")
    public String welcome(Authentication authentication, RedirectAttributes redirectAttributes, Principal principal) {

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                redirectAttributes.addFlashAttribute("welcomeMessage", "¡Bienvenido, Admin Principal!");
                return "redirect:/admin/dashboard";
            }
        }

        User currentUser = userService.getUserByCorreo(principal.getName());
        redirectAttributes.addFlashAttribute("welcomeMessage", "¡Bienvenid@, " + currentUser.getNombreCompleto() + "!");
        return "redirect:/user/home";
    }
}
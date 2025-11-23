package com.UTP.Delivery.Integrador.Controller; // O un paquete 'advice'

import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("nombreCompletoUsuario")
    public String addNombreCompletoUsuarioToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            try {
                User user = userService.getUserByCorreo(authentication.getName());
                return user.getNombreCompleto();
            } catch (Exception e) {
                return authentication.getName();
            }
        }
        return null;
    }
}
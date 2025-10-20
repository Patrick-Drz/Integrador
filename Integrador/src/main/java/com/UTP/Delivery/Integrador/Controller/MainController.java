package com.UTP.Delivery.Integrador.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
    @GetMapping("/indexUsuario")
    public String indexUsuarioPage() {
        return "indexUsuario"; //
    }
    @GetMapping("/ofertasUsuario")
    public String ofertasUsuarioPage() {return "ofertasUsuario"; }
    @GetMapping("/carritoUsuario")
    public String carritoUsuarioPage() { return "carritoUsuario"; }
    @GetMapping("/sobreNosotrosUsuario")
    public String sobreNosotrosUsuarioPage() {
        return "sobreNosotrosUsuario";
    }
    @GetMapping("/indexAdmin")
    public String indexAdminPage() { return "indexAdmin"; }
    @GetMapping("/reclamacionesAdmin")
    public String reclamacionesAdminPage() { return "reclamacionesAdmin"; }
    @GetMapping("/ventasAdmin")
    public String ventasAdminPage() { return "ventasAdmin"; }
}
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

    //@GetMapping("/compraUsuario")
    //public String compraUsuarioPage() {
    //    return "compraUsuario";
    //}

    //@GetMapping("/aulaUsuario")
    //public String aulaUsuarioPage() {return "aulaUsuario"; }

    @GetMapping("/ofertasUsuario")
    public String ofertasUsuarioPage() {return "ofertasUsuario"; }

    @GetMapping("/carritoUsuario")
    public String carritoUsuarioPage() { return "carritoUsuario"; }

    @GetMapping("/sobreNosotrosUsuario")
    public String sobreNosotrosUsuarioPage() {
        return "sobreNosotrosUsuario";
    }

    //Métodos Admin
    @GetMapping("/indexAdmin")
    public String indexAdminPage() {
        return "indexAdmin"; // Corresponde a indexAdmin.html
    }
    //@GetMapping("/menusAdmin")
    //public String menusAdminPage() { return "menusAdmin"; // Corresponde a menusAdmin.html
    //}
    @GetMapping("/pedidosAdmin")
    public String pedidosAdminPage() {
        return "pedidosAdmin"; // Corresponde a pedidosAdmin.html
    }
    //@GetMapping("/ofertasAdmin")
    //public String ofertasAdminPage() { return "ofertasAdmin"; // Corresponde a ofertasAdmin.html }
    @GetMapping("/reclamacionesAdmin")
    public String reclamacionesAdminPage() {
        return "reclamacionesAdmin"; // Corresponde a reclamacionesAdmin.html
    }
    @GetMapping("/ventasAdmin")
    public String ventasAdminPage() {
        return "ventasAdmin"; // Corresponde a ventasAdmin.html
    }
}
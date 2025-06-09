package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.Oferta;
import com.UTP.Delivery.Integrador.Model.OrdenVenta;
import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Service.OfertaService;
import com.UTP.Delivery.Integrador.Service.ProductoService;
import com.UTP.Delivery.Integrador.Service.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String UPLOAD_DIRECTORY = "./uploads/";

    @Autowired
    private ProductoService productoService;

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private VentaService ventaService;

    private boolean isAdminLoggedIn(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        return isAdmin != null && isAdmin;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado. Por favor, inicie sesión como administrador.");
            return "redirect:/login";
        }
        return "indexAdmin";
    }

    @GetMapping("/menusAdmin")
    public String showMenusAdmin(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado. Por favor, inicie sesión como administrador.");
            return "redirect:/login";
        }
        model.addAttribute("producto", new Producto());
        List<Producto> productos = productoService.getAllProductos();
        model.addAttribute("productos", productos);
        return "menusAdmin";
    }

    @PostMapping("/menu/add")
    public String addProducto(@ModelAttribute Producto producto,
                              @RequestParam("imagenFile") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }

        if (!file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String uniqueFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, uniqueFilename);
                Files.write(fileNameAndPath, file.getBytes());
                producto.setRutaImagen("/uploads/" + uniqueFilename);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("addMessage", "Error al subir la imagen: " + e.getMessage());
                redirectAttributes.addFlashAttribute("addSuccess", false);
                return "redirect:/admin/menusAdmin";
            }
        }

        try {
            productoService.saveProducto(producto);
            redirectAttributes.addFlashAttribute("addMessage", "Producto añadido correctamente.");
            redirectAttributes.addFlashAttribute("addSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addMessage", "Error al guardar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("addSuccess", false);
            e.printStackTrace();
        }
        return "redirect:/admin/menusAdmin";
    }

    @PostMapping("/menu/edit")
    public String editProducto(@ModelAttribute Producto producto,
                               @RequestParam("imagenFile") MultipartFile file,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }

        try {
            if (producto.getId() == null) {
                throw new IllegalArgumentException("ID del producto no puede ser nulo para la edición.");
            }

            if (!file.isEmpty()) {
                try {
                    String uniqueFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, uniqueFilename);
                    Files.write(fileNameAndPath, file.getBytes());
                    producto.setRutaImagen("/uploads/" + uniqueFilename);
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("editMessage", "Error al cambiar la imagen: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("editSuccess", false);
                    return "redirect:/admin/menusAdmin";
                }
            } else {
                Optional<Producto> productoExistente = productoService.getProductoById(producto.getId());
                if (productoExistente.isPresent()) {
                    producto.setRutaImagen(productoExistente.get().getRutaImagen());
                }
            }

            productoService.updateProducto(producto);
            redirectAttributes.addFlashAttribute("editMessage", "Producto modificado correctamente.");
            redirectAttributes.addFlashAttribute("editSuccess", true);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("editMessage", "Error inesperado al modificar producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editSuccess", false);
            e.printStackTrace();
        }
        return "redirect:/admin/menusAdmin";
    }

    @PostMapping("/menu/delete")
    public String deleteProducto(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }
        try {
            productoService.deleteProducto(id);
            redirectAttributes.addFlashAttribute("deleteMessage", "Producto eliminado correctamente.");
            redirectAttributes.addFlashAttribute("deleteSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteMessage", "Error al eliminar producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("deleteSuccess", false);
            e.printStackTrace();
        }
        return "redirect:/admin/menusAdmin";
    }

    // El resto de los métodos para Ofertas y Ventas no necesitan cambios
    @GetMapping("/ofertasAdmin")
    public String showOfertasAdmin(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }
        model.addAttribute("oferta", new Oferta());
        List<Oferta> ofertas = ofertaService.getAllOfertas();
        model.addAttribute("ofertas", ofertas);
        return "ofertasAdmin";
    }

    @PostMapping("/ofertas/add")
    public String addOferta(@ModelAttribute Oferta oferta, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }
        try {
            ofertaService.saveOferta(oferta);
            redirectAttributes.addFlashAttribute("addMessage", "Oferta añadida correctamente.");
            redirectAttributes.addFlashAttribute("addSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addMessage", "Error al añadir oferta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("addSuccess", false);
            e.printStackTrace();
        }
        return "redirect:/admin/ofertasAdmin";
    }

    @PostMapping("/ofertas/edit")
    public String editOferta(@ModelAttribute Oferta oferta, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }
        try {
            if (oferta.getId() == null) {
                throw new IllegalArgumentException("ID de la oferta no puede ser nulo para la edición.");
            }
            ofertaService.updateOferta(oferta);
            redirectAttributes.addFlashAttribute("editMessage", "Oferta modificada correctamente.");
            redirectAttributes.addFlashAttribute("editSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("editMessage", "Error al modificar oferta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editSuccess", false);
            e.printStackTrace();
        }
        return "redirect:/admin/ofertasAdmin";
    }

    @PostMapping("/ofertas/delete")
    public String deleteOferta(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado.");
            return "redirect:/login";
        }
        try {
            ofertaService.deleteOferta(id);
            redirectAttributes.addFlashAttribute("deleteMessage", "Oferta eliminada correctamente.");
            redirectAttributes.addFlashAttribute("deleteSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteMessage", "Error al eliminar oferta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("deleteSuccess", false);
            e.printStackTrace();
        }
        return "redirect:/admin/ofertasAdmin";
    }
    
    @GetMapping("/ventas")
    public String showVentasAdmin(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acceso denegado. Solo administradores.");
            return "redirect:/login";
        }
        try {
            List<OrdenVenta> ordenesVenta = ventaService.getAllOrdenesVenta();
            model.addAttribute("ordenesVenta", ordenesVenta);
            return "ventasAdmin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el historial de ventas: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/dashboard";
        }
    }
}
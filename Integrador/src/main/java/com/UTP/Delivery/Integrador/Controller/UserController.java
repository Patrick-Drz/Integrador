package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.*;
import com.UTP.Delivery.Integrador.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private VentaService ventaService;

    private User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos con ID: " + userId));
    }

    @GetMapping("/home")
    public String userHome(HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", currentUser);
        return "indexUsuario";
    }

    @GetMapping("/compra")
    public String showProductsAndOffers(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para ver los productos.");
            return "redirect:/login";
        }
        try {
            List<Producto> productos = productoService.getAllProductos();
            model.addAttribute("productos", productos);
            List<Oferta> ofertas = ofertaService.getAllOfertas();
            model.addAttribute("ofertas", ofertas);
            return "compraUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar los productos y ofertas: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/user/home";
        }
    }

    // --- MÉTODO CORREGIDO ---
    @GetMapping("/aula")
    public String aulaUsuarioPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para ver tu aula.");
            return "redirect:/login";
        }

        try {
            // Busca la ubicación existente o crea una nueva si no existe
            Ubicacion ubicacionParaFormulario = ubicacionService.getUbicacionPrincipalByUser(currentUser)
                    .orElse(new Ubicacion());

            // Añade el objeto al modelo. Esto es lo que necesita el th:object="${ubicacion}"
            model.addAttribute("ubicacion", ubicacionParaFormulario);

            return "aulaUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar la página del aula: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/user/home";
        }
    }

    @PostMapping("/aula/save")
    public String saveUbicacion(
            @RequestParam("piso") String piso,
            @RequestParam("codigoAula") String codigoAula,
            @RequestParam(value = "id", required = false) Long ubicacionId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para guardar tu ubicación.");
            return "redirect:/login";
        }

        try {
            ubicacionService.saveOrUpdateUbicacion(currentUser, ubicacionId, piso, codigoAula);
            redirectAttributes.addFlashAttribute("successMessage", "Ubicación guardada exitosamente!");
        } catch (IllegalArgumentException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la ubicación.");
            e.printStackTrace();
        }
        return "redirect:/user/aula";
    }

    @GetMapping("/carrito")
    public String showCarrito(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para ver tu carrito.");
            return "redirect:/login";
        }
        try {
            model.addAttribute("user", currentUser);
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            model.addAttribute("carrito", carrito);
            model.addAttribute("totalCarrito", carritoService.calcularTotalCarrito(carrito));
            Optional<Ubicacion> ubicacionOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);
            model.addAttribute("ubicacionUsuario", ubicacionOptional.orElse(null));
            if (ubicacionOptional.isEmpty()) {
                model.addAttribute("infoMessage", "No tienes una ubicación registrada. Por favor, añade una.");
            }
            return "carritoUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el carrito: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/user/home";
        }
    }

    // --- MÉTODOS AJAX (SIN CAMBIOS RESPECTO A LA VERSIÓN ANTERIOR) ---
    @PostMapping("/carrito/add")
    @ResponseBody
    public Map<String, Object> addItemToCarritoAjax(@RequestParam(value = "productoId", required = false) Long productoId, @RequestParam(value = "ofertaId", required = false) Long ofertaId, @RequestParam("cantidad") Integer cantidad, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión para añadir ítems al carrito.");
            response.put("redirectUrl", "/login");
            return response;
        }
        try {
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            carritoService.addItemToCarrito(carrito.getId(), productoId, ofertaId, cantidad);
            response.put("success", true);
            response.put("message", "Ítem(s) añadido(s) al carrito.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrito/update")
    @ResponseBody
    public Map<String, Object> updateItemQuantity(@RequestParam("itemId") Long itemId, @RequestParam("quantity") Integer quantity, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (getCurrentUser(session) == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión.");
            response.put("redirectUrl", "/login");
            return response;
        }
        try {
            carritoService.updateItemQuantity(itemId, quantity);
            response.put("success", true);
            response.put("message", "Cantidad actualizada.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrito/remove")
    @ResponseBody
    public Map<String, Object> removeItemFromCarrito(@RequestParam("itemId") Long itemId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (getCurrentUser(session) == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión.");
            response.put("redirectUrl", "/login");
            return response;
        }
        try {
            carritoService.removeItemFromCarrito(itemId);
            response.put("success", true);
            response.put("message", "Ítem eliminado del carrito.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @GetMapping("/carrito-fragment")
    public String getCarritoFragment(HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            model.addAttribute("carrito", carrito);
            model.addAttribute("totalCarrito", carritoService.calcularTotalCarrito(carrito));
            Optional<Ubicacion> ubicacionOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);
            model.addAttribute("ubicacionUsuario", ubicacionOptional.orElse(null));
        }
        return "carritoUsuario :: .carrito-container";
    }

    @PostMapping("/carrito/procesarPagoAjax")
    @ResponseBody
    public Map<String, Object> procesarPagoAjax(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión para procesar el pago.");
            response.put("redirectUrl", "/login");
            return response;
        }
        try {
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            Ubicacion ubicacion = ubicacionService.getUbicacionPrincipalByUser(currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("No tienes una ubicación de entrega registrada."));
            
            if (carrito.getItems().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tu carrito está vacío.");
                return response;
            }
            
            OrdenVenta ordenCreada = ventaService.procesarVentaDesdeCarrito(currentUser, carrito, ubicacion);
            response.put("success", true);
            response.put("message", "¡Pago procesado exitosamente! Tu orden #" + ordenCreada.getId() + " ha sido registrada.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
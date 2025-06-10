package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.Carrito;
import com.UTP.Delivery.Integrador.Model.Ubicacion;
import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Model.Oferta;
import com.UTP.Delivery.Integrador.Model.OrdenVenta;
import com.UTP.Delivery.Integrador.Model.Reclamacion;
import com.UTP.Delivery.Integrador.Service.CarritoService;
import com.UTP.Delivery.Integrador.Service.UbicacionService;
import com.UTP.Delivery.Integrador.Service.UserService;
import com.UTP.Delivery.Integrador.Service.ProductoService;
import com.UTP.Delivery.Integrador.Service.OfertaService;
import com.UTP.Delivery.Integrador.Service.VentaService;
import com.UTP.Delivery.Integrador.Service.ReclamacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
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

    @Autowired
    private ReclamacionService reclamacionService;


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


    @GetMapping("/aula")
    public String aulaUsuarioPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para ver tu aula.");
            return "redirect:/login";
        }

        try {
            Optional<Ubicacion> ubicacionActualOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);
            Ubicacion ubicacionActual = ubicacionActualOptional.orElse(null);

            model.addAttribute("ubicacionUsuario", ubicacionActual);

            Ubicacion ubicacionForm = ubicacionActualOptional.orElse(new Ubicacion());
            model.addAttribute("ubicacion", ubicacionForm);

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
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        catch (Exception e) {
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
            if (ubicacionOptional.isPresent()) {
                model.addAttribute("ubicacionUsuario", ubicacionOptional.get());
            } else {
                model.addAttribute("ubicacionUsuario", null);
                model.addAttribute("infoMessage", "No tienes una ubicación registrada. Por favor, añade una.");
            }

            return "carritoUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el carrito: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/user/home";
        }
    }

    @PostMapping("/carrito/add")
    @ResponseBody
    public Map<String, Object> addItemToCarritoAjax(
            @RequestParam(value = "productoId", required = false) Long productoId,
            @RequestParam(value = "ofertaId", required = false) Long ofertaId,
            @RequestParam("cantidad") Integer cantidad,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión para añadir ítems al carrito.");
            response.put("redirectUrl", "/login");
            return response;
        }

        try {
            if (productoId == null && ofertaId == null) {
                throw new IllegalArgumentException("Debe seleccionar un producto o una oferta para añadir al carrito.");
            }

            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            carritoService.addItemToCarrito(carrito.getId(), productoId, ofertaId, cantidad);
            response.put("success", true);
            response.put("message", "Ítem(s) añadido(s) al carrito.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al añadir ítem al carrito: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping("/carrito/update")
    public String updateItemQuantity(
            @RequestParam("itemId") Long itemId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para actualizar ítems.");
            return "redirect:/login";
        }

        try {
            carritoService.updateItemQuantity(itemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cantidad actualizada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar cantidad del ítem: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/user/carrito";
    }

    @PostMapping("/carrito/remove")
    public String removeItemFromCarrito(
            @RequestParam("itemId") Long itemId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para eliminar ítems.");
            return "redirect:/login";
        }

        try {
            carritoService.removeItemFromCarrito(itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Ítem eliminado del carrito.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar ítem del carrito: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/user/carrito";
    }

    //MÉTODO PARA PROCESAR PAGO (MODIFICADO PARA AJAX)
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
            Optional<Ubicacion> ubicacionOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);

            if (ubicacionOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "No tienes una ubicación de entrega registrada. Por favor, añádela antes de procesar el pago.");
                return response;
            }

            if (carrito.getItems().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tu carrito está vacío. No se puede procesar un pago.");
                return response;
            }

            OrdenVenta ordenCreada = ventaService.procesarVentaDesdeCarrito(currentUser, carrito, ubicacionOptional.get());

            response.put("success", true);
            response.put("message", "¡Pago procesado exitosamente! Tu orden #" + ordenCreada.getId() + " ha sido registrada.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al procesar el pago: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping("/reclamacion/enviar")
    @ResponseBody
    public Map<String, Object> enviarReclamacion(@RequestBody Reclamacion reclamacion) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reclamacion reclamacionGuardada = reclamacionService.guardarReclamacion(reclamacion);
            response.put("success", true);
            response.put("message", "¡Reclamación enviada con éxito! Nos pondremos en contacto pronto.");
            response.put("reclamacionId", reclamacionGuardada.getId()); // Opcional
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al enviar la reclamación: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}
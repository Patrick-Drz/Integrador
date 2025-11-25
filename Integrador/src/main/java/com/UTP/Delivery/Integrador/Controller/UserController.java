package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.*;
import com.UTP.Delivery.Integrador.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private CarritoService carritoService;
    @Autowired private UbicacionService ubicacionService;
    @Autowired private ProductoService productoService;
    @Autowired private OfertaService ofertaService;
    @Autowired private VentaService ventaService;
    @Autowired private ReclamacionService reclamacionService;
    @Autowired private GlobalControllerAdvice globalControllerAdvice;
    @Autowired private ContactoService contactoService;

    @GetMapping({"/", "/home"})
    public String userHome() {
        return "indexUsuario";
    }

    @GetMapping("/compra")
    public String showProductsAndOffers(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("productos", productoService.getAllProductos());
            model.addAttribute("ofertas", ofertaService.getAllOfertas());
            return "compraUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar productos.");
            return "redirect:/";
        }
    }

    @GetMapping("/aula")
    public String aulaUsuarioPage(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            Optional<Ubicacion> ubicacionActualOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);
            model.addAttribute("ubicacionUsuario", ubicacionActualOptional.orElse(null));
            model.addAttribute("ubicacion", ubicacionActualOptional.orElse(new Ubicacion()));
            return "aulaUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el aula.");
            return "redirect:/";
        }
    }

    @GetMapping("/carrito")
    public String showCarrito(Principal principal, Model model, RedirectAttributes redirectAttributes) {
         try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            model.addAttribute("user", currentUser);
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            model.addAttribute("carrito", carrito);
            model.addAttribute("totalCarrito", carritoService.calcularTotalCarrito(carrito));
            Optional<Ubicacion> ubicacionOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);
            model.addAttribute("ubicacionUsuario", ubicacionOptional.orElse(null));
            if (ubicacionOptional.isEmpty()) model.addAttribute("infoMessage", "No tienes ubicación registrada.");
            return "carritoUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el carrito.");
            return "redirect:/";
        }
    }

    @PostMapping("/aula/save")
    public String saveUbicacion(@RequestParam String piso,
                                @RequestParam String codigoAula,
                                @RequestParam(required = false) Long id,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            ubicacionService.saveOrUpdateUbicacion(currentUser, id, piso, codigoAula);
            redirectAttributes.addFlashAttribute("successMessage", "Ubicación guardada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar ubicación.");
        }
        return "redirect:/user/aula";
    }

    @PostMapping("/carrito/add")
    @ResponseBody
    public Map<String, Object> addItemToCarritoAjax(@RequestParam(required = false) Long productoId,
                                                    @RequestParam(required = false) Long ofertaId,
                                                    @RequestParam Integer cantidad,
                                                    Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            carritoService.addItemToCarrito(carrito.getId(), productoId, ofertaId, cantidad);
            response.put("success", true);
            response.put("message", "Ítem(s) añadido(s).");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrito/update")
    @ResponseBody 
    public Map<String, Object> updateItemQuantity(@RequestParam Long itemId,
                                                  @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            carritoService.updateItemQuantity(itemId, quantity);
            response.put("success", true);
            response.put("message", "Cantidad actualizada.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrito/remove")
    @ResponseBody 
    public Map<String, Object> removeItemFromCarrito(@RequestParam Long itemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            carritoService.removeItemFromCarrito(itemId);
            response.put("success", true);
            response.put("message", "Ítem eliminado.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar.");
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping("/carrito/procesarPagoAjax")
    @ResponseBody
    public Map<String, Object> procesarPagoAjax(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            Optional<Ubicacion> ubicacionOptional = ubicacionService.getUbicacionPrincipalByUser(currentUser);

            if (ubicacionOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Ubicación no registrada."); return response;
            }
            if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
                response.put("success", false);
                response.put("message", "Carrito vacío."); return response;
            }
            OrdenVenta ordenCreada = ventaService.procesarVentaDesdeCarrito(currentUser, carrito, ubicacionOptional.get());
            response.put("success", true);
            response.put("message", "Pago exitoso! Orden #" + ordenCreada.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al procesar pago: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

@PostMapping("/reclamacion/enviar")
    @ResponseBody
    public Map<String, Object> enviarReclamacion(@RequestBody Map<String, String> payload, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());

            Reclamacion reclamacion = new Reclamacion();
            reclamacion.setUsuario(currentUser); // Relación DB
            reclamacion.setTipoReclamacion(payload.get("tipoReclamacion"));
            reclamacion.setDescripcion(payload.get("descripcion"));
            
            reclamacionService.guardarReclamacion(reclamacion);
            
            response.put("success", true);
            response.put("message", "Reclamación enviada correctamente.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al enviar reclamación: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping("/contacto/enviar")
    @ResponseBody
    public Map<String, Object> enviarContacto(@RequestBody Map<String, String> payload, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());

            Contacto contacto = new Contacto();
            contacto.setUsuario(currentUser);
            contacto.setNombreCompleto(payload.get("nombreCompleto")); 
            contacto.setCorreo(payload.get("correo"));
            contacto.setMensaje(payload.get("mensaje"));

            contactoService.guardarContacto(contacto);

            response.put("success", true);
            response.put("message", "Mensaje de contacto enviado.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al enviar mensaje.");
            e.printStackTrace();
        }
        return response;
    }
}
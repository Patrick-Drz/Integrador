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

    @GetMapping("/home")
    public String userHome(Principal principal, Model model) {
        User currentUser = userService.getUserByCorreo(principal.getName());
        model.addAttribute("user", currentUser);
        return "indexUsuario";
    }

    @GetMapping("/compra")
    public String showProductsAndOffers(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("productos", productoService.getAllProductos());
            model.addAttribute("ofertas", ofertaService.getAllOfertas());
            return "compraUsuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar los productos y ofertas: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/user/home";
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
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar la página del aula: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/user/home";
        }
    }

    @PostMapping("/aula/save")
    public String saveUbicacion(@RequestParam("piso") String piso,
                                @RequestParam("codigoAula") String codigoAula,
                                @RequestParam(value = "id", required = false) Long ubicacionId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            ubicacionService.saveOrUpdateUbicacion(currentUser, ubicacionId, piso, codigoAula);
            redirectAttributes.addFlashAttribute("successMessage", "Ubicación guardada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la ubicación: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/user/aula";
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
    public Map<String, Object> addItemToCarritoAjax(@RequestParam(value = "productoId", required = false) Long productoId,
                                                    @RequestParam(value = "ofertaId", required = false) Long ofertaId,
                                                    @RequestParam("cantidad") Integer cantidad,
                                                    Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User currentUser = userService.getUserByCorreo(principal.getName());
            Carrito carrito = carritoService.getOrCreateCarritoForUser(currentUser);
            carritoService.addItemToCarrito(carrito.getId(), productoId, ofertaId, cantidad);
            response.put("success", true);
            response.put("message", "Ítem(s) añadido(s) al carrito.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al añadir ítem al carrito: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

@PostMapping("/carrito/update")
    @ResponseBody
    public Map<String, Object> updateItemQuantity(@RequestParam("itemId") Long itemId,
                                                  @RequestParam("quantity") Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            carritoService.updateItemQuantity(itemId, quantity);
            response.put("success", true);
            response.put("message", "Cantidad actualizada.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar la cantidad.");
        }
        return response;
    }

    @PostMapping("/carrito/remove")
    @ResponseBody
    public Map<String, Object> removeItemFromCarrito(@RequestParam("itemId") Long itemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            carritoService.removeItemFromCarrito(itemId);
            response.put("success", true);
            response.put("message", "Ítem eliminado del carrito.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Hubo un error al eliminar el ítem.");
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
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al enviar la reclamación: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}
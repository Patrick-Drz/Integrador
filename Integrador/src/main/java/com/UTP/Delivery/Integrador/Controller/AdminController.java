package com.UTP.Delivery.Integrador.Controller;

import com.UTP.Delivery.Integrador.Model.Oferta;
import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Model.Reclamacion;
import com.UTP.Delivery.Integrador.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    @Autowired
    private ReclamacionService reclamacionService;
    @Autowired
    private ContactoService contactoService; 
    @Autowired
    private ReporteService reporteService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "indexAdmin";
    }

    @GetMapping("/menusAdmin")
    public String showMenusAdmin(Model model) {
        model.addAttribute("producto", new Producto());
        List<Producto> productos = productoService.getAllProductos();
        model.addAttribute("productos", productos);
        return "menusAdmin";
    }

    @PostMapping("/menu/add")
    public String addProducto(@ModelAttribute Producto producto,
                              @RequestParam("imagenFile") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String uniqueFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path fileNameAndPath = uploadPath.resolve(uniqueFilename);
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
                               RedirectAttributes redirectAttributes) {
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
                    return "redirect:/admin/menusAdmin";
                }
            } else {
                productoService.getProductoById(producto.getId())
                        .ifPresent(p -> producto.setRutaImagen(p.getRutaImagen()));
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
    public String deleteProducto(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
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

    @GetMapping("/ofertasAdmin")
    public String showOfertasAdmin(Model model) {
        model.addAttribute("oferta", new Oferta());
        model.addAttribute("ofertas", ofertaService.getAllOfertas());
        return "ofertasAdmin";
    }

    @PostMapping("/ofertas/add")
    public String addOferta(@ModelAttribute Oferta oferta, RedirectAttributes redirectAttributes) {
        try {
            if (oferta.getActiva() == null) {
                oferta.setActiva(false);
            }
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
    public String editOferta(@ModelAttribute Oferta oferta, RedirectAttributes redirectAttributes) {
        try {
            if (oferta.getId() == null) {
                throw new IllegalArgumentException("ID de la oferta no puede ser nulo para la edición.");
            }
            if (oferta.getActiva() == null) {
                oferta.setActiva(false);
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
    public String deleteOferta(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
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
    public String showVentasAdmin(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("ordenesVenta", ventaService.getAllOrdenesVenta());
            return "ventasAdmin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el historial de ventas: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/reclamaciones")
    public String showReclamacionesAdmin(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("reclamaciones", reclamacionService.getAllReclamaciones());
            return "reclamacionesAdmin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar las reclamaciones: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/contactos")
    public String showContactosAdmin(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("contactos", contactoService.getAllContactos());
            return "contactosAdmin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar los mensajes de contacto: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/reportes/ventas-excel")
    public ResponseEntity<Resource> descargarReporteVentasExcel() {
        try {
            ByteArrayOutputStream excelStream = reporteService.generarReporteVentasExcel();
            ByteArrayResource resource = new ByteArrayResource(excelStream.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reportes/reclamaciones-excel")
    public ResponseEntity<Resource> descargarReporteReclamacionesExcel() {
        try {
            ByteArrayOutputStream excelStream = reporteService.generarReporteReclamacionesExcel();
            ByteArrayResource resource = new ByteArrayResource(excelStream.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_reclamaciones.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.UTP.Delivery.Integrador.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal; // <-- Asegúrate de que esta importación esté presente

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_producto", unique = true, nullable = false)
    private String codigoProducto;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    // --- CORRECCIÓN: CAMBIADO DE Double a BigDecimal ---
    @Column(nullable = false, precision = 10, scale = 2) // Es buena práctica definir precisión y escala para decimales
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "ruta_imagen")
    private String rutaImagen;
}
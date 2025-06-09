package com.UTP.Delivery.Integrador.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_oferta", unique = true, nullable = false, length = 50)
    private String codigoOferta;

    @Column(name = "nombre_oferta", nullable = false, length = 255)
    private String nombreOferta;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_regular", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioRegular;

    @Column(name = "precio_oferta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activa;

}

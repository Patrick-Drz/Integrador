package com.UTP.Delivery.Integrador.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detalles_orden_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_venta", nullable = false)
    private OrdenVenta ordenVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oferta")
    private Oferta oferta;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioAlMomento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public DetalleOrdenVenta(OrdenVenta ordenVenta, Producto producto, Oferta oferta, Integer cantidad, BigDecimal precioUnitarioAlMomento) {
        this.ordenVenta = ordenVenta;
        this.producto = producto;
        this.oferta = oferta;
        this.cantidad = cantidad;
        this.precioUnitarioAlMomento = precioUnitarioAlMomento;
        this.subtotal = precioUnitarioAlMomento.multiply(BigDecimal.valueOf(cantidad));
    }
}
package com.UTP.Delivery.altumcaffe.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name = "items_carrito")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"carrito"})
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oferta")
    private Oferta oferta;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_al_momento", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioAlMomento;

    public boolean isProducto() {
        return this.producto != null;
    }

    public boolean isOferta() {
        return this.oferta != null;
    }

    public BigDecimal getTotalItemPrice() {
        if (precioUnitarioAlMomento == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitarioAlMomento.multiply(BigDecimal.valueOf(cantidad));
    }
}
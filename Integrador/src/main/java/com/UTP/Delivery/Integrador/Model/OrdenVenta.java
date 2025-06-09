package com.UTP.Delivery.Integrador.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ordenes_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Column(nullable = false)
    private LocalDateTime fechaOrden;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_entrega")
    private Ubicacion ubicacionEntrega;

    @OneToMany(mappedBy = "ordenVenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleOrdenVenta> items = new ArrayList<>();

    public OrdenVenta(User usuario, LocalDateTime fechaOrden, BigDecimal total, Ubicacion ubicacionEntrega) {
        this.usuario = usuario;
        this.fechaOrden = fechaOrden;
        this.total = total;
        this.ubicacionEntrega = ubicacionEntrega;
        this.items = new ArrayList<>();
    }

    public void setItems(List<DetalleOrdenVenta> items) {
        this.items.clear();
        if (items != null) {
            for (DetalleOrdenVenta item : items) {
                this.items.add(item);
                item.setOrdenVenta(this);
            }
        }
    }
}

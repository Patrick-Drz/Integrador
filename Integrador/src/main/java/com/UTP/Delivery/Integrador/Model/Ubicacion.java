package com.UTP.Delivery.Integrador.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "ubicaciones")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Column(name = "piso", nullable = false)
    private String piso;

    @Column(name = "codigo_aula", unique = true, nullable = false)
    private String codigoAula;

    public Ubicacion(User usuario, String piso, String codigoAula) {
        this.usuario = usuario;
        this.piso = piso;
        this.codigoAula = codigoAula;
    }
}
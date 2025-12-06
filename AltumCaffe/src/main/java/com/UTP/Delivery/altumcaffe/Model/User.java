package com.UTP.Delivery.altumcaffe.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor 
@ToString(exclude = {"contrasena"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "codigo_estudiante", unique = true, nullable = false)
    private String codigoEstudiante;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "activo")
    private Boolean activo = true;

    public User(String correo, String contrasena, String nombreCompleto, String codigoEstudiante) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.codigoEstudiante = codigoEstudiante;
        this.rol = "ROLE_USER"; 
    }

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }
}
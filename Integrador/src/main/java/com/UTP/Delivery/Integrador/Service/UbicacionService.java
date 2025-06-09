package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.Ubicacion;
import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Transactional
    public Ubicacion saveOrUpdateUbicacion(User user, Long ubicacionId, String piso, String codigoAula) {
        Ubicacion ubicacion;

        if (ubicacionId != null) {
            ubicacion = ubicacionRepository.findById(ubicacionId)
                    .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada con ID: " + ubicacionId));
            if (!ubicacion.getUsuario().getId().equals(user.getId())) { // Assumes getUsuario() returns User and getId() exists
                throw new SecurityException("No tienes permiso para modificar esta ubicación.");
            }
            ubicacion.setPiso(piso);
            ubicacion.setCodigoAula(codigoAula);
        } else {
            Optional<Ubicacion> existingUbicacionForUser = ubicacionRepository.findByUsuario(user); // Assumes findByUsuario returns an Optional<Ubicacion>

            if (existingUbicacionForUser.isPresent()) {
                ubicacion = existingUbicacionForUser.get();
                ubicacion.setPiso(piso);
                ubicacion.setCodigoAula(codigoAula);
            } else {
                ubicacion = new Ubicacion();
                ubicacion.setUsuario(user);
                ubicacion.setPiso(piso);
                ubicacion.setCodigoAula(codigoAula);
            }
        }

        Optional<Ubicacion> existingByCodeForUser = ubicacionRepository.findByUsuarioAndCodigoAula(user, codigoAula);
        if (existingByCodeForUser.isPresent()) {
            if (ubicacion.getId() == null || !existingByCodeForUser.get().getId().equals(ubicacion.getId())) {
                throw new IllegalArgumentException("Ya tienes una ubicación con el código de aula '" + codigoAula + "'.");
            }
        }

        return ubicacionRepository.save(ubicacion);
    }

    public Optional<Ubicacion> getUbicacionPrincipalByUser(User user) {
        return ubicacionRepository.findByUsuario(user);
    }

    public Optional<Ubicacion> getUbicacionById(Long id) {
        return ubicacionRepository.findById(id);
    }

    public List<Ubicacion> getAllUbicaciones() {
        return ubicacionRepository.findAll();
    }
}
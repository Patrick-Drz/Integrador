package com.UTP.Delivery.altumcaffe.Service;

import com.UTP.Delivery.altumcaffe.Model.Oferta;
import com.UTP.Delivery.altumcaffe.Repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    public List<Oferta> getAllOfertas() {
        return ofertaRepository.findAll();
    }

    public Optional<Oferta> getOfertaById(Long id) {
        return ofertaRepository.findById(id);
    }

    public Oferta saveOferta(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }

    public void deleteOferta(Long id) {
        ofertaRepository.deleteById(id);
    }

    public Oferta updateOferta(Oferta oferta) {
        if (oferta.getId() == null || !ofertaRepository.existsById(oferta.getId())) {
            throw new IllegalArgumentException("La oferta con ID " + oferta.getId() + " no existe.");
        }
        return ofertaRepository.save(oferta);
    }
}
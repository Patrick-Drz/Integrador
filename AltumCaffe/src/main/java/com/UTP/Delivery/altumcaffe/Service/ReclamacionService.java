package com.UTP.Delivery.altumcaffe.Service;

import com.UTP.Delivery.altumcaffe.Model.Reclamacion;
import com.UTP.Delivery.altumcaffe.Repository.ReclamacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReclamacionService {

    @Autowired
    private ReclamacionRepository reclamacionRepository;

    @Transactional
    public Reclamacion guardarReclamacion(Reclamacion reclamacion) {
        return reclamacionRepository.save(reclamacion);
    }

    @Transactional(readOnly = true)
    public List<Reclamacion> getAllReclamaciones() {
        return reclamacionRepository.findAll();
    }
}

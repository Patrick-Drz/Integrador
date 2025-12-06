package com.UTP.Delivery.altumcaffe.Service;

import com.UTP.Delivery.altumcaffe.Model.Contacto;
import com.UTP.Delivery.altumcaffe.Repository.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ContactoService {
    @Autowired
    private ContactoRepository contactoRepository;

    @Transactional
    public Contacto guardarContacto(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    @Transactional(readOnly = true)
    public List<Contacto> getAllContactos() {
        return contactoRepository.findAll();
    }
}
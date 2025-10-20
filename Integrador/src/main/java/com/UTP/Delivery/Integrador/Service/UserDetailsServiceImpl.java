// Ubicación: src/main/java/com/UTP/Delivery/Integrador/Service/UserDetailsServiceImpl.java

package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.User;
import com.UTP.Delivery.Integrador.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró usuario con el correo: " + email));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRol());

        return new org.springframework.security.core.userdetails.User(
                user.getCorreo(),
                user.getContrasena(),
                Collections.singletonList(authority)
        );
    }
}
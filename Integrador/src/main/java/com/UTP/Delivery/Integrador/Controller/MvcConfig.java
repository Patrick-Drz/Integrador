package com.UTP.Delivery.Integrador.Controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // El patrón /uploads/** mapea a la carpeta 'uploads' en el directorio raíz del proyecto
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
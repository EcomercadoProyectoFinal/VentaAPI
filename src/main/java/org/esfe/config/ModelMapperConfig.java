package org.esfe.config;

import org.esfe.dtos.venta.VentaSalida;
import org.esfe.modelos.Venta;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        // Configuración para mapear el nombre del MétodoPago en el DTO de VentaSalida
        modelMapper.createTypeMap(Venta.class, VentaSalida.class)
            // Origen: Venta -> MetodoPago -> MetodoPago (nombre del método)
            // Destino: VentaSalida -> setNombreMetodoPago()
            .addMapping(src -> src.getMetodoPago().getMetodoPago(), VentaSalida::setNombreMetodoPago);

        return modelMapper;
    }
}
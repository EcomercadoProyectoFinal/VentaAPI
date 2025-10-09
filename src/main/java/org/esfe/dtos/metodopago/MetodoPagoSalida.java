package org.esfe.dtos.metodopago;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPagoSalida implements Serializable {
    private Integer id;

    private String metodoPago;
    
    private String descripcion;
}

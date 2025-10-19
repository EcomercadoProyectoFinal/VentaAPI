package org.esfe.dtos.detallesVentas;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaSalida implements Serializable {
    
    private Integer id;

    private Long idProducto; 

    private String nombreProducto;
    
    private Integer cantidad;
    
    private BigDecimal precioUnitario; 
    
    private BigDecimal subtotal; 
}
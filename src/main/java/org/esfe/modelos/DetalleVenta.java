package org.esfe.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal; 

@Getter
@Setter
@Entity
@Table(name = "detalleVentas") 
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación ManyToOne con la entidad Venta
    @ManyToOne
    @JoinColumn (name = "idVenta", nullable = false)
    private Venta venta;

    @NotNull
    private Long idProducto; 

    @NotNull
    private String nombreProducto;

    @NotNull
    private Integer cantidad;

    @NotNull
    private BigDecimal precioUnitario; 
    
    @NotNull
    private BigDecimal subtotal; 
}
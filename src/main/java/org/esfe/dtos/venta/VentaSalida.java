package org.esfe.dtos.venta;

import lombok.Getter;
import lombok.Setter;

import org.esfe.dtos.detallesVentas.DetalleVentaSalida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

@Getter
@Setter
public class VentaSalida implements Serializable {
    
    // Campos generados y de identificación
    private Integer id;
    private String correlativo;
    private LocalDateTime fecha;
    
    // Campos de dinero y estado
    private BigDecimal total;
    private String estado;
    private Boolean pagoConfirmadoEmpresa;
    
    // Referencias a IDs
    private Long usuarioId;
    private Long idEmpresaVendedora;
    private Long idBroker;

    // Relación de Método de Pago simplificada (Solo el nombre/ID)
    private Integer idMetodoPago;
    private String nombreMetodoPago;
    
    private List<DetalleVentaSalida> detalles;
}
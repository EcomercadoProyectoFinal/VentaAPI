package org.esfe.dtos.venta;

import org.esfe.dtos.detallesVentas.DetalleVentaGuardar; 

import java.io.Serializable;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaGuardar implements Serializable {
    @NotNull(message = "El método de pago es obligatorio.")
    private Integer idMetodoPago; 

    private Long idEmpresaVendedora;
    private Long idBroker;
    private Long usuarioId;

    @Valid
    @NotNull(message = "La venta debe contener al menos un detalle de producto.")
    private List<DetalleVentaGuardar> detalles;
}
